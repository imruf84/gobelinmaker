#include <SoftwareSerial.h>
#include <AltSoftSerial.h>

SoftwareSerial chat1(10, 11);
AltSoftSerial chat2(8, 9);

String serialString = "";
String chat1String = "";
String chat2String = "";

void setup()  
{
  Serial.begin(9600);
  while(!Serial);
  Serial.println("Starting...");
  
  chat1.begin(9600);
  delay(1000);
  chat1.println("Test message");
  
  chat2.begin(9600);
  delay(1000);
  chat2.println("Test message");
  
  Serial.println("OK");
}

void loop() 
{

  if (chat1.available()) 
  {
    char inChar = (char)chat1.read();
    if (inChar == '\n') {
      Serial.print("RECEIVED1: ");
      Serial.print(chat1String);
      chat1String = "";
    }else{
      chat1String += inChar;
    }
  }

  if (chat2.available()) 
  {
    char inChar = (char)chat2.read();
    if (inChar == '\n') {
      Serial.print("RECEIVED2: ");
      Serial.print(chat2String);
      chat2String = "";
    }else{
      chat2String += inChar;
    }
  }

  if (Serial.available()) {
    char inChar = (char)Serial.read();
    if (inChar == '\n') {
      switch (serialString.charAt(0))
      {
        case '1':
          Serial.print("SEND1: ");
          chat1.println(serialString);
        break;
        case '2':
          Serial.print("SEND2: ");
          chat2.println(serialString);
        break;
      }
      Serial.println(serialString);
      serialString = "";
    }else{
      serialString += inChar;
    }
  }
  
}
