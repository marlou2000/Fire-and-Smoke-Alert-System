#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>                 
#include <FirebaseArduino.h>         
#include <SoftwareSerial.h>

#define FIREBASE_HOST "smoke-and-fire-alert-system-default-rtdb.firebaseio.com"      
#define FIREBASE_AUTH "hcWiOLb0fV9njyTVUwYtHSWnu5piF0Wmxfc1rrGa"            
#define WIFI_SSID "Galaxy A01 Core9067"                                  
#define WIFI_PASSWORD "samsung01" 

HTTPClient http;

String homeID = "2023001";
String deviceID = "202300101";
String area;
String addressDefault = "Not set";
String headFirstName = "First Name";
String headMiddleName = "Middle Name";
String headLastName = "Last Name";
String numberDefault = "09691922320";
String ip = "192.168.43.66/currentMonitoringSystem";

String receivedString;

int loopCounter = 0;

bool foundHomeID = false;
bool foundDeviceID = false;

bool loopOnce = true;

void setup() 
{
  Serial.begin(115200); 
  Serial1.begin(9600);
  
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);                                  
  Serial.print("Connecting to ");
  Serial.print(WIFI_SSID);
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
 
  Serial.println();
  Serial.print("Connected");
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
}

void loop() 
{
  int counterLoop = 0;
  int counterLoop1 = 0;
  int numberOfFound = 0;


      if(loopOnce == true){

              


              
              //get Default area which is the are at the first selection
              for(;;){
                    http.begin("http://"+ ip + "/2.php");
                    http.addHeader("Content-Type", "application/x-www-form-urlencoded");
              
                    int httpCodeArea = http.GET();
                  
                    if (httpCodeArea == HTTP_CODE_OK) {
                      String responseArea = http.getString();
                      
                     int areaIndex = responseArea.indexOf(',');
                     String areaRecieved = responseArea.substring(0, areaIndex);
              
                     area = areaRecieved;
              
                     Serial.println("Area Recieved: " + area);
                     break;
                    } 
                    
                    else {
                        Serial.println("Retrieving Area ");
                    }
              }
              //*****************


            //ma get anay sya bago sya ma push ka information
                for(;;){
                  http.begin("http://"+ ip + "/8.php");
                  http.addHeader("Content-Type", "application/x-www-form-urlencoded");
                  String dataHomeIDgetInformation = "data=" + homeID;
                  String dataInformation = dataHomeIDgetInformation; // your data goes here
                  
                  int httpCodeInformation = http.POST(dataInformation);
                          
                  if (httpCodeInformation == HTTP_CODE_OK) {
                         String responseInformation = http.getString();
                         Serial.println("Information Found : " + responseInformation);
                         
                         if(responseInformation == "Nothing Found"){
                            Firebase.setString("/Home/HomeID/"+homeID+"/Information/Address", addressDefault);
                            Firebase.setString("/Home/HomeID/"+homeID+"/Information/Head/FirstName", headFirstName);
                            Firebase.setString("/Home/HomeID/"+homeID+"/Information/Head/MiddleName", headMiddleName);
                            Firebase.setString("/Home/HomeID/"+homeID+"/Information/Head/LastName", headLastName);
                            break;
                         }

                         else if(responseInformation == "Found One"){
                            Serial.println("Information Exist Already");
                            break;
                         }

                         else{
                            //sending data to firebase
                              Firebase.setInt("/Home/HomeID/"+homeID+"/DeviceID/"+deviceID+"/Fire", 2);
                              Firebase.setInt("/Home/HomeID/"+homeID+"/DeviceID/"+deviceID+"/Smoke", 0);
                              Firebase.setInt("/Home/HomeID/"+homeID+"/DeviceID/"+deviceID+"/Alert", 0);
                              Firebase.setString("/Home/HomeID/"+homeID+"/DeviceID/"+deviceID+"/Area", area);
                            //
                         }    
                   } 
                            
                   else {
                      Serial.println("Trying to Retrieve Information Existence");
                   }
                }
            //************
      
      
            //ma get anay sya bago sya ma push ka number
                for(;;){
                  http.begin("http://"+ ip + "/9.php");
                  http.addHeader("Content-Type", "application/x-www-form-urlencoded");
                  String dataHomeIDgetNumber = "data=" + homeID;
                  String dataNumber = dataHomeIDgetNumber; // your data goes here
                  
                  int httpCodeNumber = http.POST(dataNumber);
                          
                  if (httpCodeNumber == HTTP_CODE_OK) {
                         String responseNumber = http.getString();
                         Serial.println("Number Found : " + responseNumber);
      
                         if(responseNumber == "Nothing Found"){
                            Firebase.pushString("/Home/HomeID/"+homeID+"/Number", numberDefault);
                         }

                         else{
                            Serial.println("Number Exist Already");
                         }
                         
                         break;
                   } 
                            
                   else {
                      Serial.println("Trying to Retrieve Number Existence");
                   }
                }
            //************
      
            //ma get anay sya bago sya ma push ka Alert
                for(;;){
                  http.begin("http://"+ ip + "/10.php");
                  http.addHeader("Content-Type", "application/x-www-form-urlencoded");
                  String dataHomeIDgetTotalAlert = "data=" + homeID;
                  String dataTotalAlert = dataHomeIDgetTotalAlert; // your data goes here
                  
                  int httpCodeTotalAlert = http.POST(dataTotalAlert);
                          
                  if (httpCodeTotalAlert == HTTP_CODE_OK) {
                         String responseTotalAlert = http.getString();
                         Serial.println("Number Found : " + responseTotalAlert);
      
                         if(responseTotalAlert == "Nothing Found"){
                            Firebase.setInt("/Home/HomeID/"+homeID+"/Alert", 0);
                         }

                         else{
                            Serial.println("Alert Exist Already");
                         }
                         
                         break;
                   } 
                            
                   else {
                      Serial.println("Trying to Retrieve Alert Existence");
                   }
                }
            //************


            //ma get anay sya bago sya ma push ka Pin Code
                for(;;){
                  http.begin("http://"+ ip + "/11.php");
                  http.addHeader("Content-Type", "application/x-www-form-urlencoded");
                  String dataHomeIDgetPinCode = "data=" + homeID;
                  String dataPinCode = dataHomeIDgetPinCode; // your data goes here
                  
                  int httpCodePinCode = http.POST(dataPinCode);
                          
                  if (httpCodePinCode == HTTP_CODE_OK) {
                         String responsePinCode = http.getString();
                         Serial.println("Number Found : " + responsePinCode);
      
                         if(responsePinCode == "Nothing Found"){
                            Firebase.setInt("/Home/HomeID/"+homeID+"/PinCode", 1234);
                         }

                         else{
                            Serial.println("Pin Code Exist Already");
                         }
                         
                         break;
                   } 
                            
                   else {
                      Serial.println("Trying to Retrieve Alert Existence");
                   }
                }
            //************


           loopOnce = false;

              
      }


      
  if (Serial.available() > 0) {

      receivedString = Serial.readStringUntil('%');

      Serial.println("Recieved String : " + receivedString);
      
      int index = receivedString.indexOf(',');
      String fire = receivedString.substring(0, index);
      int fireInt = fire.toInt();
      
      String fireRemove = removeWord(receivedString, fire);
      String removeFirstCharacter = fireRemove.substring(1);
      int smokeIndex = removeFirstCharacter.indexOf(',');
      String smoke = removeFirstCharacter.substring(0, smokeIndex);
      int smokeInt = smoke.toInt();

      String smokeRemove = removeWord(removeFirstCharacter, smoke);
      String alert = smokeRemove.substring(1);
      int alertInt = alert.toInt();

      Serial.println("Fire : " + fire);
      Serial.println("Smoke : " + smoke);
      Serial.println("Alert : " + alert);


      //sending data to firebase
      Firebase.setInt("/Home/HomeID/"+homeID+"/DeviceID/"+deviceID+"/Fire", fireInt);
      Firebase.setInt("/Home/HomeID/"+homeID+"/DeviceID/"+deviceID+"/Smoke", smokeInt);
      Firebase.setInt("/Home/HomeID/"+homeID+"/DeviceID/"+deviceID+"/Alert", alertInt);
      Firebase.setString("/Home/HomeID/"+homeID+"/DeviceID/"+deviceID+"/Area", area);
      //


      //check and set Total Alert for turning on and off all the relays of devices
          http.begin("http://"+ ip + "/6.php");
          http.addHeader("Content-Type", "application/x-www-form-urlencoded");
          String dataHomeID = "data=" + homeID;
          String data = dataHomeID; // your data goes here
          
          int httpCodeTotalAlert = http.POST(data);
                  
          if (httpCodeTotalAlert == HTTP_CODE_OK) {
                 String responseTotalAlert = http.getString();
                 Serial.println("Total Alert Value : " + responseTotalAlert);
           } 
                    
           else {
              Serial.println("Failed To Retrieve Total Alert Value");
           }
      //*************************



      //retrieve ang area if nag change baka sa kali gin edit ka user
          http.begin("http://"+ ip + "/7.php");
          http.addHeader("Content-Type", "application/x-www-form-urlencoded");
          String dataHomeAndDeviceID = "data=" + homeID + "," + deviceID;
          Serial.println(dataHomeAndDeviceID);
          String dataHomeAndDeviceIDToPost = dataHomeAndDeviceID; // your data goes here
          
          int httpCodeAreaUpdated = http.POST(dataHomeAndDeviceIDToPost);
                  
          if (httpCodeAreaUpdated == HTTP_CODE_OK) {
                 String responseAreaUpdated = http.getString();
                 Serial.println("Updated Area : " + responseAreaUpdated);
                 area = responseAreaUpdated;
           } 
                    
           else {
              Serial.println("Failed To Retrieve Updated Area");
           }
      //*************************
      
      

            //get the alert value    
            //http.begin("http://fireandsmoke.000webhostapp.com/getDeviceID.php");
           http.begin("http://"+ ip + "/4.php");
            http.addHeader("Content-Type", "application/x-www-form-urlencoded");
          
            int httpCodeAlert = http.POST(data);
            
            if (httpCodeAlert == HTTP_CODE_OK) {
                
                String responseAlert = http.getString();

                if(responseAlert == "1" || alert == "1"){
                        
                        if(responseAlert == "1"){
                              //if there is alert then it will retrieve all the numbers
                            
                              //get all the numbers in the firebase
                              String dataHomeID = "data=" + homeID;
                      
                              //http.begin("http://fireandsmoke.000webhostapp.com/getDeviceID.php");
                              http.begin("http://"+ ip + "/5.php");
                              http.addHeader("Content-Type", "application/x-www-form-urlencoded");
                              String data = dataHomeID; // your data goes here
                            
                              int httpCodeNumbers = http.POST(data);
                              
                              if (httpCodeNumbers == HTTP_CODE_OK) {
                                  String responseNumbers = http.getString();

                                  http.begin("http://"+ ip + "/9.php");
                                  http.addHeader("Content-Type", "application/x-www-form-urlencoded");
                                  String dataHomeIDgetNumber = "data=" + homeID;
                                  String dataNumber = dataHomeIDgetNumber; // your data goes here
                                  
                                  int httpCodeNumber = http.POST(dataNumber);
                                          
                                  if (httpCodeNumber == HTTP_CODE_OK) {
                                         String responseNumber = http.getString();
                                         Serial.println("Number Found : " + responseNumber);
                      
                                         if(responseNumber == "Nothing Found"){
                                            Serial1.print(area + ",4,%");
                                            Serial.print("Sending: " + area + ",4,");
                                         }
                
                                         else{
                                             Serial1.print(area + ",4," + responseNumbers + "%");
                                         }
                                   } 
                                            
                                   else {
                                      Serial.println("Trying to Retrieve Number Existence");
                                   }
                  
                                  
                              } 
                              
                              else {
                                  Serial.print("Did not able to get all the numbers");
                                  Serial.print("Sending Error Signal 41%");
                                  Serial1.print("41%");
                                  delay(2000);
                              }
            
                              //**********************   
                        }


                        else if(alert == "1"){
                          //if there is alert then it will retrieve all the numbers
                            
                              //get all the numbers in the firebase
                              String dataHomeID = "data=" + homeID;
                      
                              //http.begin("http://fireandsmoke.000webhostapp.com/getDeviceID.php");
                              http.begin("http://"+ ip + "/5.php");
                              http.addHeader("Content-Type", "application/x-www-form-urlencoded");
                              String data = dataHomeID; // your data goes here
                            
                              int httpCodeNumbers = http.POST(data);
                              
                              if (httpCodeNumbers == HTTP_CODE_OK) {
                                  String responseNumbers = http.getString();

                                  http.begin("http://"+ ip + "/9.php");
                                  http.addHeader("Content-Type", "application/x-www-form-urlencoded");
                                  String dataHomeIDgetNumber = "data=" + homeID;
                                  String dataNumber = dataHomeIDgetNumber; // your data goes here
                                  
                                  int httpCodeNumber = http.POST(dataNumber);
                                          
                                  if (httpCodeNumber == HTTP_CODE_OK) {
                                         String responseNumber = http.getString();
                                         Serial.println("Number Found : " + responseNumber);
                      
                                         if(responseNumber == "Nothing Found"){
                                            Serial1.print(area + ",5,%");
                                            Serial.print("Sending: " + area + ",5,");
                                         }
                
                                         else{
                                             Serial1.print(area + ",5," + responseNumbers + "%");
                                             Serial.print("Sending: " + area + ",5," + responseNumbers);
                                         }
                                   } 
                                            
                                   else {
                                      Serial.println("Trying to Retrieve Number Existence");
                                   }
                              } 
                              
                              else {
                                  Serial.print("Did not able to get all the numbers");
                                  Serial.print("Sending Error Signal 51%");
                                  Serial1.print("51%");
                                  delay(2000);
                              }
            
                              //**********************   
                        }
                                    
                }

                else{
                  Serial.print("Sending 0%");
                  Serial1.print("0%");
                }
                
            } 

            
            
            else {
                Serial.print("Did not able to get the alert value");
                delay(2000);

                if(alert == "1"){
                  //if there is alert then it will retrieve all the numbers
                        
                        //get all the numbers in the firebase
                        String dataHomeID = "data=" + homeID;
                
                        //http.begin("http://fireandsmoke.000webhostapp.com/getDeviceID.php");
                        http.begin("http://"+ ip + "/5.php");
                        http.addHeader("Content-Type", "application/x-www-form-urlencoded");
                        String data = dataHomeID; // your data goes here
                      
                        int httpCodeNumbers = http.POST(data);
                        
                        if (httpCodeNumbers == HTTP_CODE_OK) {
                            String responseNumbers = http.getString();

                            
                                  http.begin("http://"+ ip + "/9.php");
                                  http.addHeader("Content-Type", "application/x-www-form-urlencoded");
                                  String dataHomeIDgetNumber = "data=" + homeID;
                                  String dataNumber = dataHomeIDgetNumber; // your data goes here
                                  
                                  int httpCodeNumber = http.POST(dataNumber);
                                          
                                  if (httpCodeNumber == HTTP_CODE_OK) {
                                         String responseNumber = http.getString();
                                         Serial.println("Number Found : " + responseNumber);
                      
                                         if(responseNumber == "Nothing Found"){
                                            Serial1.print(area + ",5,%");
                                            Serial.print("Sending: " + area + ",5,");
                                         }
                
                                         else{
                                             Serial1.print(area + ",5," + responseNumbers + "%");
                                             Serial.print("Sending: " + area + ",5," + responseNumbers);
                                         }
                                   } 
                                            
                                   else {
                                      Serial.println("Trying to Retrieve Number Existence");
                                   }
                        } 
                        
                        else {
                            Serial.print("Did not able to get all the numbers");
                            Serial.print("Sending Error Signal 51%");
                            Serial1.print("51%");

                            delay(2000);
                        }
                }

                else{
                    Serial.print("Sending 0%");
                    Serial1.print("0%");
                }
                
            }
          //**********************
  }

  Serial.println("");
  Serial.println("");
  Serial.println("");
  
  delay(5000);
}

String removeWord(String str, String word) {
  int index = str.indexOf(word);
  if (index == -1) return str;
  int len = word.length();
  return str.substring(0, index) + str.substring(index+len);
}
