String inputString = "";
boolean stringComplete = false;

/*
#include <AccelStepper.h>

typedef struct s_stepper {
  String name;
  AccelStepper stepper;
  bool running;
} t_stepper;

t_stepper steppers[] = {
  {"LiftRight",AccelStepper(AccelStepper::DRIVER,2,3),false},
  {"ArmRight0",AccelStepper(AccelStepper::DRIVER,4,5),false},
};
uint8_t steppersCount = sizeof(steppers)/sizeof(t_stepper);

void setupSteppers() 
{
  for (uint8_t i = 0; i < steppersCount; i++)
  {
    steppers[i].stepper.setMaxSpeed(2000);
    steppers[i].stepper.setAcceleration(2000);
  }
}
*/
String getValue(String data, char separator, int index)
{
    int found = 0;
    int strIndex[] = { 0, -1 };
    int maxIndex = data.length() - 1;

    for (int i = 0; i <= maxIndex && found <= index; i++) {
        if (data.charAt(i) == separator || i == maxIndex) {
            found++;
            strIndex[0] = strIndex[1] + 1;
            strIndex[1] = (i == maxIndex) ? i+1 : i;
        }
    }
    return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}

bool waitingForSteppers = false;
/*
t_stepper* getStepperByName(String name)
{
  for (uint8_t i = 0; i < steppersCount; i++)
  {
    if (steppers[i].name.equals(name)) return &steppers[i];
  }

  return NULL;
}
*/

void setup() {
  Serial.begin(9600);
  inputString.reserve(200);

//  setupSteppers();
}

void loop() {
  if (stringComplete) {

    String command = "";

    command = "getID";
    if (inputString.startsWith(command)) {
      Serial.println("gm");
    }

    // General command.
    command = ":";
    if (inputString.startsWith(command)) {
      inputString = inputString.substring(command.length());
      inputString.toUpperCase();
      Serial.println(inputString);
    }

    // Motor command: @motor_name steps
    command = "@";
    if (inputString.startsWith(command)) {
      inputString = inputString.substring(command.length());
      String name = getValue(inputString, ' ', 0);
      int steps = getValue(inputString, ' ', 1).toInt();

/*
      t_stepper *stepper = getStepperByName(name);
      if (NULL != stepper) {

        if (name.equals("m1"))
        {
          stepper->stepper.moveTo(steps);
        }
        
        if (name.startsWith("Lift"))
        {
          stepper->stepper.move(steps);
        }
        
        stepper->running = true;
        waitingForSteppers = true;
      }
      else
      {
        Serial.println(String("[ERROR] There are no stepper motor with name: " + name));
      }*/
      
    }
    
    inputString = "";
    stringComplete = false;
  }


  // Handle steppers logic.
  if (waitingForSteppers) {
    waitingForSteppers = false;
/*    for (uint8_t i = 0; i < steppersCount; i++)
    {
      steppers[i].stepper.run();
      if (steppers[i].stepper.distanceToGo() == 0) 
      {
        steppers[i].running = false;
      }

      waitingForSteppers = waitingForSteppers || steppers[i].running;
    }
*/
    if (!waitingForSteppers)
    {
      Serial.println("[INFO] Steppers finished.");
    }
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


