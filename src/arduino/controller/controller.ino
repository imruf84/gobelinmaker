String inputString = "";
boolean stringComplete = false;

void setup() {
  Serial.begin(9600);
  inputString.reserve(200);
}

void loop() {
  if (stringComplete) {

    String command = "";

    command = "getID";
    if (inputString.startsWith(command)) {
      Serial.println("gm");
    }

    command = ":";
    if (inputString.startsWith(command)) {
      inputString = inputString.substring(command.length());
      inputString.toUpperCase();
      Serial.println(inputString);
    }
    
    inputString = "";
    stringComplete = false;
  }
}

void serialEvent() {
  while (Serial.available()) {
    char inChar = (char)Serial.read();
    if (inChar == '\n') {
      stringComplete = true;
    } else {
      inputString += inChar;
    }
  }
}


