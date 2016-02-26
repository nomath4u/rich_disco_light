#define ANALOG_MAX 255

/*These need to be PWM pins*/
int green_pin = 3;
int red_pin = 5;
int blue_pin = 6;


void setup(){
  pinMode(green_pin, OUTPUT);
  pinMode(red_pin, OUTPUT);
  pinMode(blue_pin, OUTPUT);
}

void loop(){
  int i = 0;
  for(i=0; i < ANALOG_MAX; i++){ //Conveniently using analog_max because it is the same max number
    Wheel(i);
    delay(10);
  }
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
