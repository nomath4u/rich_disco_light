#define ANALOG_MAX 255

/*These need to be PWM pins*/
int green_pin = D1;
int red_pin = D2;
int blue_pin = D0;
int led_pin = D7;
bool wheel = false;
bool blink_red = false;

void setup(){
  pinMode(green_pin, OUTPUT);
  pinMode(red_pin, OUTPUT);
  pinMode(blue_pin, OUTPUT);
  pinMode(led_pin, OUTPUT);
  Particle.function("green", net_green);
  Particle.function("red", net_red);
  Particle.function("blue", net_blue);
  Particle.function("colors", net_set_colors);
  Particle.function("special", net_special);
}

void loop(){
  int i = 0;
  if(wheel){
    for(i=0; i < ANALOG_MAX; i++){ //Conveniently using analog_max because it is the same max number
        Wheel(i);
        delay(10);
    }
  }
  if(blink_red){
      clear_colors();
      set_red(255);
      delay(100);
      set_red(0);
      delay(1000);
  }
  //digitalWrite(led_pin, HIGH);
}

void Wheel(byte WheelPos) {
  WheelPos = 255 - WheelPos;
  if(WheelPos < 85) {
   color(255 - WheelPos * 3, 0, WheelPos * 3);
  } else if(WheelPos < 170) {
    WheelPos -= 85;
   color(0, WheelPos * 3, 255 - WheelPos * 3);
  } else {
   WheelPos -= 170;
   color(WheelPos * 3, 255 - WheelPos * 3, 0);
  }
}

void clear_colors(){
    set_red(0);
    set_green(0);
    set_blue(0);
}
void color(uint8_t red, uint8_t green, uint8_t blue){
  set_red(red);
  set_green(green);
  set_blue(blue);
}

void set_red(uint8_t red){
  analogWrite(red_pin, red);
}

void set_green(uint8_t green){
  analogWrite(green_pin, green);
}

void set_blue(uint8_t blue){
  analogWrite(blue_pin, blue);
}

int net_green(String val){
    set_green(val.toInt());
}

int net_red(String val){
    set_red(val.toInt());
}

int net_blue(String val){
    set_blue(val.toInt());
}

/*This function expects a comma separated string in the form of "red,green,blue" */
int net_set_colors(String vals){
    int commaIndex = vals.indexOf(',');
    int secondCommaIndex = vals.indexOf(',', commaIndex+1);
    String red_str = vals.substring(0, commaIndex);
    String green_str = vals.substring(commaIndex+1, secondCommaIndex);
    String blue_str = vals.substring(secondCommaIndex+1); 
    set_red(red_str.toInt());
    set_green(green_str.toInt());
    set_blue(blue_str.toInt());
    turn_off_specials();
    return blue_str.toInt();
}

void turn_off_specials(){
    wheel = false;
    blink_red = false;
}

int net_special(String command){
    turn_off_specials();
    if(command == "Wheel"){
        wheel = true;
    }
    if(command == "Blink Red"){
        blink_red = true;
    }
}
