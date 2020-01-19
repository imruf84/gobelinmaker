#include <RotaryEncoder.h>

RotaryEncoder encoder(A2, A3);
#define PIN_A 5
#define PIN_B 6

int maxRealSpeed = 1000;
int minRealSpeed = 150;
int realSpeed = 0;
int maxSpeedToSet = 100;
int minSpeedToSet = 1;
int speedToSet = 0;
int direction = 1;
String serialString = "";

void setup()
{
  Serial.begin(9600);
  while (!Serial);
  
  PCICR |= (1 << PCIE1);
  PCMSK1 |= (1 << PCINT10) | (1 << PCINT11);

  randomSeed(analogRead(0));

  pinMode(PIN_A, OUTPUT);
  pinMode(PIN_B, OUTPUT);

  setSpeed(0);
}

ISR(PCINT1_vect) 
{
  encoder.tick();
}

void doForwardStep() 
{
  analogWrite(PIN_A, 255);
  analogWrite(PIN_B, 0);
}

void doBackwardStep() 
{
  analogWrite(PIN_A, 0);
  analogWrite(PIN_B, 255);
}

void doStopStep() 
{
  analogWrite(PIN_A, 0);
  analogWrite(PIN_B, 0);
}

void setDirection(int d) {
  direction = d;
}

int getDirection() {
  return direction;
}

void setRealSpeed(int s)
{
  realSpeed = s;
}

void setSpeed(int s) 
{
  speedToSet = s;
  setRealSpeed(abs(s));
  
  if (getSpeed() == 0) 
  {
    setDirection(0);
    return;
  }

  setRealSpeed(max(minSpeedToSet, min(maxSpeedToSet, realSpeed)));
  setRealSpeed(map(realSpeed, minSpeedToSet, maxSpeedToSet, minRealSpeed, maxRealSpeed));

  if (getSpeed() > 0) 
  {
    setDirection(1);
    return;
  }

  setDirection(-1);
}

int getSpeed() 
{
  return speedToSet;
}

int getRealSpeed() 
{
  return realSpeed;
}

long targetPosition = 0;

void loop()
{

  long currentPosition = encoder.getPosition();
  long distance = currentPosition-targetPosition;
  if (abs(distance) <= 1) 
  {
    setSpeed(0);
    //delay(100);
    //encoder.setPosition(currentPosition);
    //targetPosition = currentPosition;
  } 
  else
  {
    setSpeed(-distance);
  }
  
  if (getRealSpeed() != 0) 
  {
    if (getDirection() > 0) 
    {
      doForwardStep();
    } 
    else 
    {
      doBackwardStep();
    }
    delayMicroseconds(getRealSpeed());
    doStopStep();
    delayMicroseconds(maxRealSpeed-getRealSpeed());
  } 
  else 
  {
    doStopStep();
  }

  if (Serial.available()) 
  {
    char inChar = (char)Serial.read();
    if (inChar == '\n') 
    {
      int s;
      if (serialString == "r") 
      {
        //s = random(-maxSpeedToSet,maxSpeedToSet);
        s = random(-2000, 2000);
        targetPosition = s;
        //setSpeed(s);
      } 
      else 
      {
        s = serialString.toInt();
        targetPosition = s;
        //setSpeed(s);
      }
      Serial.print(s);
      Serial.print(" -> ");
      Serial.println(getDirection()*getRealSpeed());
      serialString = "";
    }
    else
    {
      serialString += inChar;
    }
  }
  
}

