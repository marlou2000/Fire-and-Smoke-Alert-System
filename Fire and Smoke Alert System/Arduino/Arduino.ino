#include <SoftwareSerial.h>

SoftwareSerial gsmSerial(7, 8); // RX, TX
SoftwareSerial espSerial(3, 4);

//gsm
String receivedString;
String headNumber = "+639696262252";
String deviceID = "202300101";

int alert = 0;

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

bool loopStop = false;
bool serialWasRead = false;

int loopCount = 0;

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

    digitalWrite(relay, LOW); // turning the relay on
    
    digitalWrite(ledPinFlameSignal, LOW);
    digitalWrite(ledPinSmokeSignal, LOW);
    digitalWrite(ledPinConnectionSignal, LOW);
    digitalWrite(ledPinErroConnectionSignal, LOW);
    digitalWrite(ledPinSerialSignal, LOW);
}

void loop() {

      sensorValue = analogRead(0); 
      int flame = digitalRead(flamePin);

          //FIRE and Smoke alarm check
          if (flame == 0 || sensorValue > 200){
              delay(5000);
              loopCount = loopCount + 5;

              sensorValue = analogRead(0); 
              int flame = digitalRead(flamePin);

              if(flame == 0 && sensorValue > 200){
                  digitalWrite(relay, HIGH);
                  digitalWrite(ledPinFlameSignal, HIGH);
                  digitalWrite(ledPinSmokeSignal, HIGH); 
                  alert = 1;
                  
                  gsmSerial.println("AT+CMGF=1"); 
                  delay(1000);
                  gsmSerial.println("AT+CMGS=\"" + headNumber + "\""); 
                  delay(1000);
                  gsmSerial.println("Emergency Alert Fire and Smoke Detected on Device ID " + deviceID); 
                  gsmSerial.println((char)26); // send control-Z
                  delay(1000);
                  
              }

              else if(flame == 0 && sensorValue <= 200 ){
                  digitalWrite(relay, HIGH);
                  digitalWrite(ledPinFlameSignal, HIGH);
                  digitalWrite(ledPinSmokeSignal, LOW); 
                  alert = 1;
 
                  gsmSerial.println("AT+CMGF=1"); 
                  delay(1000);
                  gsmSerial.println("AT+CMGS=\"" + headNumber + "\""); 
                  delay(1000);
                  gsmSerial.println("Emergency Alert Fire Detected on Device ID " + deviceID); 
                  gsmSerial.println((char)26); // send control-Z
                  delay(1000);
              }

              else if(flame == 1 && sensorValue > 200){
                  digitalWrite(relay, HIGH);
                  digitalWrite(ledPinFlameSignal, LOW);  
                  digitalWrite(ledPinSmokeSignal, HIGH); 
                  alert = 1;
                  
                  gsmSerial.println("AT+CMGF=1"); 
                  delay(1000);
                  gsmSerial.println("AT+CMGS=\"" + headNumber + "\""); 
                  delay(1000);
                  gsmSerial.println("Emergency Alert Smoke Detected on Device ID " + deviceID); 
                  gsmSerial.println((char)26); // send control-Z
                  delay(1000);
              }
          
              else{
                digitalWrite(relay, LOW);
                digitalWrite(ledPinFlameSignal, LOW);
                digitalWrite(ledPinSmokeSignal, LOW);
    
                alert = 0;
              }
          }

          else{
                digitalWrite(relay, LOW);
                digitalWrite(ledPinFlameSignal, LOW);
                digitalWrite(ledPinSmokeSignal, LOW);
    
                alert = 0;
          }
       //********************

      if(loopCount >= 10){
          digitalWrite(ledPinSerialSignal, HIGH);
          String sensorStringValues = String(String(flame) + "," + String(sensorValue) + "," + String(alert) + "%");
          espSerial.print(sensorStringValues);
          loopCount = 0;
          delay(2000);
          digitalWrite(ledPinSerialSignal, LOW);
      }  


    if (Serial.available() > 0) {
      receivedString = Serial.readStringUntil('%');
      Serial.println("Reieved Value : " + receivedString);
      Serial.println("String Lenght : " + String(receivedString.length()));
      int stringLength = receivedString.length();

      char incomingByte = Serial.read();
      receivedString += incomingByte;

      if(receivedString == "0"){
          Serial.println("Recieved No Alert");
      }

      else if(receivedString != "0" && stringLength > 0 && receivedString != "" && receivedString != " "){
          int areaIndex = receivedString.indexOf(',');
          String area = receivedString.substring(0, areaIndex);
          
          String areaRemove = removeWord(receivedString, area);
          String removeFirstCharacter = areaRemove.substring(1);
          int alertIndex = removeFirstCharacter.indexOf(',');
          String alert = removeFirstCharacter.substring(0, alertIndex); 

          String alertRemove = removeWord(removeFirstCharacter, alert);
          String removeFirstCharacter1 = alertRemove.substring(1);
                
          Serial.println("Area:" + area);
          Serial.println("Alert:" + alert);
          
          sensorValue = analogRead(0); 
          int flame = digitalRead(flamePin);

          
          //ibig sabihin magamit ko internet pag send sang number
          if(alert == "4"){
            Serial.println("Sending To all and alert is on");
            digitalWrite(relay, HIGH);
            
                for(;;){
                if(removeFirstCharacter1 == " " || removeFirstCharacter1  == ""){
                  break;
                }
                int numberIndex = removeFirstCharacter1.indexOf(',');
                String number = removeFirstCharacter1.substring(0, numberIndex); 
                     
                String numberRemove = removeWord(removeFirstCharacter1, number);
                String removeFirstCharacter2 = numberRemove.substring(1);
                removeFirstCharacter1 = removeFirstCharacter2;
    
                Serial.println("Number:" + number);
    
                if(sensorValue > 200 && flame == 0){
                        gsmSerial.println("AT+CMGF=1"); 
                        delay(1000);
                        gsmSerial.println("AT+CMGS=\"" + number + "\""); 
                        delay(1000);
                        gsmSerial.println("Emergency Alert Fire and Smoke Detected on " + area); 
                        gsmSerial.println((char)26); // send control-Z
                        delay(1000);
                    }
    
                    else if(sensorValue > 200 && flame == 1){
                        gsmSerial.println("AT+CMGF=1"); 
                        delay(1000);
                        gsmSerial.println("AT+CMGS=\"" + number + "\""); 
                        delay(1000);
                        gsmSerial.println("Emergency Alert Smoke Detected on " + area); 
                        gsmSerial.println((char)26); // send control-Z
                        delay(1000);
                    }
    
                    else if(sensorValue <= 200 && flame == 0){
                        gsmSerial.println("AT+CMGF=1"); 
                        delay(1000);
                        gsmSerial.println("AT+CMGS=\"" + number + "\""); 
                        delay(1000);
                        gsmSerial.println("Emergency Alert Fire Detected on " + area); 
                        gsmSerial.println((char)26); // send control-Z
                        delay(1000);
                    }
                    
                delay(3000);
                
                }
          }


          //ibig sabihin di ko magamit net mag send sa nubmers pero i on ang total relay
            else if(alert == "41"){
                 digitalWrite(relay, HIGH);
                Serial.println("Sending To head and alert is on");
            }


            //ibig sabihin magamit net mag send sa nubmers pero i on ang relay unless kung may alert sa mga device i off lang
            else if(alert == "5"){
                Serial.println("Sending To all numbers but it is not on alert");
                for(;;){
                  if(removeFirstCharacter1 == " " || removeFirstCharacter1 == ""){
                    break;
                  }
                  

                  int numberIndex = removeFirstCharacter1.indexOf(',');
                  String number = removeFirstCharacter1.substring(0, numberIndex); 
                       
                  String numberRemove = removeWord(removeFirstCharacter1, number);
                  String removeFirstCharacter2 = numberRemove.substring(1);
                  removeFirstCharacter1 = removeFirstCharacter2;
      
                  Serial.println("Number:" + number);
      
                  if(sensorValue > 200 && flame == 0){
                          gsmSerial.println("AT+CMGF=1"); 
                          delay(1000);
                          gsmSerial.println("AT+CMGS=\"" + number + "\""); 
                          delay(1000);
                          gsmSerial.println("Emergency Alert Fire and Smoke Detected on " + area); 
                          gsmSerial.println((char)26); // send control-Z
                          delay(1000);
                          digitalWrite(relay, HIGH);
                      }
      
                      else if(sensorValue > 200 && flame == 1){
                          gsmSerial.println("AT+CMGF=1"); 
                          delay(1000);
                          gsmSerial.println("AT+CMGS=\"" + number + "\""); 
                          delay(1000);
                          gsmSerial.println("Emergency Alert Smoke Detected on " + area); 
                          gsmSerial.println((char)26); // send control-Z
                          delay(1000);
                          digitalWrite(relay, HIGH);
                      }
      
                      else if(sensorValue <= 200 && flame == 0){
                          gsmSerial.println("AT+CMGF=1"); 
                          delay(1000);
                          gsmSerial.println("AT+CMGS=\"" + number + "\""); 
                          delay(1000);
                          gsmSerial.println("Emergency Alert Fire Detected on " + area); 
                          gsmSerial.println((char)26); // send control-Z
                          delay(1000);
                          digitalWrite(relay, HIGH);
                      }

                      delay(3000);
                }
            }


            //ibig sabihin di ko magamit net mag send sa nubmers pero i on ang relay unless kung may alert sa mga device i off lang
            else if(alert == "51"){
                Serial.println("Sending To head but it is not on alert");
            }
      }

      else{
          Serial.println("Recieved Nothing");
      }
    }

    loopCount++;

    delay(1000);
}

String removeWord(String str, String word) {
  int index = str.indexOf(word);
  if (index == -1) return str;
  int len = word.length();
  return str.substring(0, index) + str.substring(index+len);
}
