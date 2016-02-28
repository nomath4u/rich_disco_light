package com.nomath4u.disco_light;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private SeekBar red_bar;
    private SeekBar green_bar;
    private SeekBar blue_bar;
    private int red_val = 0;
    private int green_val = 0;
    private int blue_val = 0;
    private static String device_id = "";
    private static String access_token = "";
    private String spinner_val = "Wheel"; //This should be whatever the first one in the list is.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        red_bar = (SeekBar) findViewById(R.id.seekBar_red);
        green_bar = (SeekBar) findViewById(R.id.seekBar_green);
        blue_bar = (SeekBar) findViewById(R.id.seekBar_blue);
        setup_seekbars();
        setup_spinner();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void send_color(View view){
        HashMap<String, String> postDataParams = new HashMap<String, String>();
        String data_string = "";
        postDataParams.put("access_token", access_token);
        postDataParams.put("params", Integer.toString(red_val) + "," + Integer.toString(green_val) + "," +Integer.toString(blue_val)); //This is where you send the seek bar value
        try {
            data_string = getPostDataString(postDataParams);
        } catch (Exception e){
            Log.d("Fail", "postdatastring");
        }


        new PhotonCommunicatorTask().execute("https://api.particle.io/v1/devices/" + device_id + "/colors", data_string);
    }

    public void send_special(View view){
        HashMap<String, String> postDataParams = new HashMap<String, String>();
        String data_string = "";
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.getSelectedItem();

        postDataParams.put("access_token", access_token);
        postDataParams.put("params", spinner_val ); //Name of the special you want
        try {
            data_string = getPostDataString(postDataParams);
        } catch (Exception e){
            Log.d("Fail", "postdatastring");
        }

        new PhotonCommunicatorTask().execute("https://api.particle.io/v1/devices/" + device_id + "/special", data_string);
    }



    public void setup_seekbars(){

        red_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                red_val = progress;
                TextView red_view = (TextView) findViewById(R.id.textView_red);
                red_view.setText("Red: " + Integer.toString(red_val));

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                //If you want the color to auto change this is where to do it
            }
        });

        green_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                green_val = progress;
                TextView green_view = (TextView) findViewById(R.id.textView_green);
                green_view.setText("Green: " + Integer.toString(green_val));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                //If you want the color to auto change this is where to do it
            }
        });

        blue_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                blue_val = progress;
                TextView blue_view = (TextView) findViewById(R.id.textView_blue);
                blue_view.setText("Blue: " + Integer.toString(blue_val));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                //If you want the color to auto change this is where to do it
            }
        });
    }
    private void setup_spinner(){
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.special_functions, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        spinner_val = parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet())
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}

class PhotonCommunicatorTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
        return performPostCall(params[0], params[1]);
        }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        //May want a toast here
    }
    public String  performPostCall(String requestURL, String post_data_string) {

        URL url;
        String response = "";

        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(post_data_string);

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }
}

class SpinnerActivity extends Activity  {

}

