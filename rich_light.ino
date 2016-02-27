#define ANALOG_MAX 255

/*These need to be PWM pins*/
int green_pin = D1;
int red_pin = D2;
int blue_pin = D0;
int led_pin = D7;

void setup(){
  pinMode(green_pin, OUTPUT);
  pinMode(red_pin, OUTPUT);
  pinMode(blue_pin, OUTPUT);
  pinMode(led_pin, OUTPUT);
  Particle.function("green", net_green);
  Particle.function("red", net_red);
  Particle.function("blue", net_blue);
}

void loop(){
  int i = 0;
  for(i=0; i < ANALOG_MAX; i++){ //Conveniently using analog_max because it is the same max number
    //Wheel(i);
    delay(10);
  }
  digitalWrite(led_pin, HIGH);
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

int net_green(String val){
    set_green(val.toInt());
}

int net_red(String val){
    set_red(val.toInt());
}

int net_blue(String val){
    set_blue(val.toInt());
}
