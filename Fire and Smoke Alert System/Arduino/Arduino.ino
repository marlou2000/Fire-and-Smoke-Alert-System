#include <SoftwareSerial.h>

SoftwareSerial gsmSerial(7, 8); // RX, TX
SoftwareSerial espSerial(3, 4);

//gsm
String receivedString;
String number = "+639912981231";

String area = "Kitchen";

int fireAlert = 0, smokeAlert = 0;

const int relay = 6;  

//sensors
const int flamePin = 5;
int sensorValue;

//leds signal
const int ledPinFlameSignal =  9;
const int ledPinSmokeSignal =  10;
const int ledPinConnectionSignal =  2;
const int ledPinErroConnectionSignal = 11;
const int ledPinSerialSignal = 13;

int loopCounter = 0;
bool serialWasRead = false;

void setup() {
    gsmSerial.begin(9600);
    espSerial.begin(115200);
    Serial.begin(9600);
    
    pinMode(relay, OUTPUT);
    pinMode(flamePin, INPUT);
    pinMode(ledPinFlameSignal, OUTPUT);
    pinMode(ledPinSmokeSignal, OUTPUT);
    pinMode(ledPinConnectionSignal, OUTPUT);
    pinMode(ledPinErroConnectionSignal, OUTPUT);
    pinMode(ledPinSerialSignal, OUTPUT);

    digitalWrite(relay, HIGH); // turning the relay on
    digitalWrite(ledPinFlameSignal, LOW);
    digitalWrite(ledPinSmokeSignal, LOW);
    digitalWrite(ledPinConnectionSignal, LOW);
    digitalWrite(ledPinErroConnectionSignal, LOW);
    digitalWrite(ledPinSerialSignal, LOW);
}

void loop() {
    
    if(loopCounter == 0 || serialWasRead == true){
      sensorValue = analogRead(0); 
      int flame = digitalRead(flamePin);
    
          //FIRE and Smoke alarm check
          if (flame == 0 || sensorValue > 200){
            digitalWrite(relay, HIGH);

              if(flame == 0){
                digitalWrite(ledPinFlameSignal, HIGH);
                fireAlert = 1;
              }
          
              if(sensorValue > 200){
                digitalWrite(ledPinSmokeSignal, HIGH);
                smokeAlert = 1;
              }
          }
          
          else{
            digitalWrite(relay, LOW);
            digitalWrite(ledPinFlameSignal, LOW);
            digitalWrite(ledPinSmokeSignal, LOW);
            
            fireAlert = 0;
            smokeAlert = 0;
          }
          //******
      
          String sensorStringValues = String(String(flame) + "," + String(sensorValue)) + "," + area;
          espSerial.println(sensorStringValues);

          delay(5000);
          
          loopCounter++;
    }
    

    if (Serial.available() > 0) {
      digitalWrite(ledPinSerialSignal, HIGH); 
      
      serialWasRead = true;
      
//      char incomingByte = Serial.read();
//      receivedString += incomingByte;
      receivedString = Serial.readStringUntil('\n');
      
      int index = receivedString.indexOf(',');
      String connection = receivedString.substring(0, index);
      
      String removeConnectionResponse = removeWord(receivedString, connection);
      String removeFirstCharacter = removeConnectionResponse.substring(1);
      int alertIndex = removeFirstCharacter.indexOf(',');
      String alertString = removeFirstCharacter.substring(0, alertIndex);
      int alertInt = alertString.toInt();

      String removeAlertIntResponse = removeWord(removeFirstCharacter, alertString);
      String number = removeAlertIntResponse.substring(1);

      int numberCount = 1;
      
      //count how many numbers there is
      for(;;){
            if(number == ""){
              break;
            }
  
            else{
              numberCount++;
              int numberIndex = number.indexOf(',');
              String numberString = number.substring(0, numberIndex);
              String removeNumberFromString = removeWord(number, numberString);
              number = removeNumberFromString.substring(1);
            }
      }      


        //check if there is internet connection or 
        if(connection == "Ok"){
          digitalWrite(ledPinConnectionSignal, HIGH); 
          
           sensorValue = analogRead(0); 
           int flame = digitalRead(flamePin);
    
          //checking if fire or smoke gets triggered
            if(fireAlert == 1 && smokeAlert == 1){
              gsmSerial.println("AT+CMGF=1"); 
              delay(1000);
              gsmSerial.println("AT+CMGS=\"" + number + "\""); 
              delay(1000);
              gsmSerial.println("Emergency Alert Fire and Smoke Detected on " + area); 
              gsmSerial.println((char)26); // send control-Z
              delay(1000);
            }
        
            //checking if smoke gets triggered
            else if(fireAlert == 0 && smokeAlert == 1){
              gsmSerial.println("AT+CMGF=1"); 
              delay(1000);
              gsmSerial.println("AT+CMGS=\"" + number + "\""); 
              delay(1000);
              gsmSerial.println("Emergency Alert Smoke Detected on " + area); 
              gsmSerial.println((char)26); // send control-Z
              delay(1000);
            }
        
            //checking if fire gets triggered
            else if(fireAlert == 1 && smokeAlert == 0){
              gsmSerial.println("AT+CMGF=1"); 
              delay(1000);
              gsmSerial.println("AT+CMGS=\"" + number + "\""); 
              delay(1000);
              gsmSerial.println("Emergency Alert Fire Detected on " + area); 
              gsmSerial.println((char)26); // send control-Z
              delay(1000);
            }

            delay(2000);
             digitalWrite(ledPinConnectionSignal, LOW); 
        }

        else{
          digitalWrite(ledPinErroConnectionSignal, HIGH);
          sensorValue = analogRead(0); 
          int flame = digitalRead(flamePin);
          
          
          //checking if fire or smoke gets triggered
            if(fireAlert == 1 && smokeAlert == 1){
              gsmSerial.println("AT+CMGF=1"); 
              delay(1000);
              gsmSerial.println("AT+CMGS=\"" + number + "\""); 
              delay(1000);
              gsmSerial.println("Emergency Alert Fire and Smoke Detected on " + area); 
              gsmSerial.println((char)26); // send control-Z
              delay(1000);
            }
        
            //checking if smoke gets triggered
            if(fireAlert == 0 && smokeAlert == 1){
              gsmSerial.println("AT+CMGF=1"); 
              delay(1000);
              gsmSerial.println("AT+CMGS=\"" + number + "\""); 
              delay(1000);
              gsmSerial.println("Emergency Alert Smoke Detected on " + area); 
              gsmSerial.println((char)26); // send control-Z
              delay(1000);
            }
        
            //checking if fire gets triggered
            if(fireAlert == 1 && smokeAlert == 0){
              gsmSerial.println("AT+CMGF=1"); 
              delay(1000);
              gsmSerial.println("AT+CMGS=\"" + number + "\""); 
              delay(1000);
              gsmSerial.println("Emergency Alert Fire Detected on " + area); 
              gsmSerial.println((char)26); // send control-Z
              delay(1000);
            }

            delay(2000);
             digitalWrite(ledPinErroConnectionSignal, LOW); 
        }  
         
        delay(2000);
        digitalWrite(ledPinSerialSignal, LOW ); 
    }
}

String removeWord(String str, String word) {
  int index = str.indexOf(word);
  if (index == -1) return str;
  int len = word.length();
  return str.substring(0, index) + str.substring(index+len);
}
