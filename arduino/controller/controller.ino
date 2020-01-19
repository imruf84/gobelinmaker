#define DEVICE_ID "gm"

#include "MillisTimer.h"
int t = 2000;
MillisTimer timer1 = MillisTimer();
void myTimerFunction(MillisTimer &mt)
{
  //Serial.print(Constant("Repeat: "));
  //Serial.println(mt.getRemainingRepeats());

  Serial.println(mt.getRemainingRepeats());
  t=sqrt(t);
  mt.setInterval(t);
}

#include <SoftwareSerial.h>
#include <AltSoftSerial.h>

SoftwareSerial node1(10, 11);
AltSoftSerial node2(8, 9);

String serialString = "";
String node1String = "";
String node2String = "";

void setup()  
{
  Serial.begin(9600);
  while(!Serial) continue;
  Serial.println("Starting...");
  
  node1.begin(9600);
  delay(1000);
  node1.println("Test message");
  
  node2.begin(9600);
  delay(1000);
  node2.println("Test message");
  
  Serial.println("OK");




  timer1.setInterval(t);
  timer1.expiredHandler(myTimerFunction);
  timer1.setRepeats(20);
  timer1.start();
}

void loop() 
{
  
  timer1.run();

  // Előző node üzenetének a feldolgozása.
  if (node1.available()) 
  {
    char inChar = (char)node1.read();
    if (inChar == '\n') {
      Serial.print("RECEIVED1: ");
      Serial.print(node1String);
      node1String = "";
    }else{
      node1String += inChar;
    }
  }

  // Következő node üzenetének a feldolgozása.
  if (node2.available()) 
  {
    char inChar = (char)node2.read();
    if (inChar == '\n') {
      Serial.print("RECEIVED2: ");
      Serial.print(node2String);
      node2String = "";
    }else{
      node2String += inChar;
    }
  }

  // Soros porton csatlakoztatott node üzenetének a feldolgozása.
  if (Serial.available()) {
    char inChar = (char)Serial.read();
    if (inChar == '\n') {

      // Eszközazonosító küldése.
      if (serialString.startsWith("getID")) {
        Serial.println(DEVICE_ID);
      } else {
        // Komplexebb adatok feldolgozása.
        Serial.println(serialString);
      }
      
      serialString = "";
    }else{
      serialString += inChar;
    }
  }
  
}
