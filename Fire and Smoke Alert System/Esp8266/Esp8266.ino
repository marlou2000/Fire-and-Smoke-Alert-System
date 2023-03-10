#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>                 
#include <FirebaseArduino.h>         
#include <SoftwareSerial.h>

#define FIREBASE_HOST "smoke-and-fire-alert-system-default-rtdb.firebaseio.com"      
#define FIREBASE_AUTH "hcWiOLb0fV9njyTVUwYtHSWnu5piF0Wmxfc1rrGa"            
#define WIFI_SSID "Galaxy A01 Core9067"                                  
#define WIFI_PASSWORD "samsung01"            

HTTPClient http;

String deviceID = "20191240";
String receivedString;

int loopCounter = 0;

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
  
  if (Serial.available() > 0) {
      receivedString = Serial.readStringUntil('\n');
      
      int index = receivedString.indexOf(',');
      String flame = receivedString.substring(0, index);
      int fireInt = flame.toInt();
      
      String smoke = removeWord(receivedString, flame);
      String smokeFinal = smoke.substring(1);
      int index1 = smokeFinal.indexOf(',');
      String smokeValue = smokeFinal.substring(0, index1);
      int smokeInt = smokeValue.toInt();

      String removeSmoke = removeWord(smokeFinal, smokeValue);
      String area = removeSmoke.substring(1);
      
      //String area = removeWord(receivedString, flame);
      //String smokeFinal = smoke.substring(1);
      //int smokeInt = smokeFinal.toInt();

      http.begin("http://fireandsmoke.000webhostapp.com/index.php");
      int httpCode = http.GET();
      
      if (httpCode == HTTP_CODE_OK) {
          String response = http.getString(); // this is the response from API
          String copyOfResponse = response; // copy of response to be used for concatination
          
          //for getting the amount of Device ID 
          for(;;){
                int comma = response.indexOf(","); //get the string until comma
                String result = response.substring(0, comma); //Get the first deviceID
                int lengthOfResult = strlen(result.c_str()); //Get the length of the id
                int lengthOfResultFinal = lengthOfResult + 1; //Get the length of the id + 1
                int lengthOfResponse = strlen(response.c_str()); //Get the total length of the response
                response = response.substring(lengthOfResultFinal, lengthOfResponse); //getting the new response withouth the first ID          
                if(result == ""){
                  break;
                }
               counterLoop++;
          }

          String deviceIDArray[counterLoop];
    
          //for storing the Device ID into an DeviceArray of String
          for(int i = 0; i < counterLoop; i++){
              int comma = copyOfResponse.indexOf(","); //get the string until comma
              String result = copyOfResponse.substring(0, comma); //Get the first deviceID
              int lengthOfResult = strlen(result.c_str()); //Get the length of the id
              int lengthOfResultFinal = lengthOfResult + 1; //Get the length of the id + 1
              int lengthOfResponse = strlen(copyOfResponse.c_str()); //Get the total length of the response
              copyOfResponse = copyOfResponse.substring(lengthOfResultFinal, lengthOfResponse); //getting the new response withouth the first ID 
              
              deviceIDArray[i] = result;
          }

           //For checking if there is already id number in firebase database
          for(int z = 0; z < counterLoop; z++){                
              if(deviceID == deviceIDArray[z]){
                numberOfFound++;
                break;
              }
          }
          //*****

          //if greater than 0 it means it already exist
          if(numberOfFound == 0){
              Firebase.set("/Data/"+deviceID, "");
              Firebase.setString("/Data/"+deviceID + "/Area", area);
      
              Firebase.pushString("/DeviceID/id", deviceID); // this is for adding a new deviceID into device ID list
          }


          //For getting alerts and numbers from firebase
          http.begin("http://fireandsmoke.000webhostapp.com/index1.php");
          int httpCode1 = http.GET();
      
          if (httpCode1 == HTTP_CODE_OK) {
              String response1 = http.getString(); // this is the response from API
              String copyOfResponse1 = response1; // copy of response to be used for concatination
              int alertValueInt = Firebase.getInt("/Data/" + deviceID + "/Alert");
              String alertValueString = String(alertValueInt);

              if(response1 == ""){
                response1 = "0";
                Serial1.println("Ok," + alertValueString + "," + response1);
              }

              else{
                Serial1.println("Ok," + alertValueString + "," + response1);
              }
              
//              //for getting the amount of Device ID 
//              for(;;){
//                    int comma1 = response1.indexOf(","); //get the string until comma
//                    String result1 = response1.substring(0, comma1); //Get the first deviceID
//                    int lengthOfResult1 = strlen(result1.c_str()); //Get the length of the id
//                    int lengthOfResultFinal1 = lengthOfResult1 + 1; //Get the length of the id + 1
//                    int lengthOfResponse1 = strlen(response1.c_str()); //Get the total length of the response
//                    response1 = response1.substring(lengthOfResultFinal1, lengthOfResponse1); //getting the new response withouth the first ID          
//                    if(result1 == ""){
//                      break;
//                    }
//                   counterLoop1++;
//              }
//    
//              String numberArray[counterLoop1];
//        
//              //for storing the Device ID into an DeviceArray of String
//              for(int z = 0; z < counterLoop1; z++){
//                  int comma1 = copyOfResponse1.indexOf(","); //get the string until comma
//                  String result1 = copyOfResponse1.substring(0, comma1); //Get the first deviceID
//                  int lengthOfResult1 = strlen(result1.c_str()); //Get the length of the id
//                  int lengthOfResultFinal1 = lengthOfResult1 + 1; //Get the length of the id + 1
//                  int lengthOfResponse1 = strlen(copyOfResponse1.c_str()); //Get the total length of the response
//                  copyOfResponse1 = copyOfResponse1.substring(lengthOfResultFinal1, lengthOfResponse1); //getting the new response withouth the first ID 
//                  
//                  numberArray[z] = result1;
//              }
          }


          else{
            Serial1.println("Error,0,0");
          }
      }

      else {
        Serial1.println("Error,0,0");
      }

      //This line of code will add or set a new data to fire and smoke cell
      Firebase.setInt("/Data/"+deviceID + "/Smoke", smokeInt);
      Firebase.setInt("/Data/"+deviceID + "/Fire", fireInt);
      //********

      //This is a condition if there is need to send emergency or not
      if(flame == "0  " || smokeInt > 200){
          Firebase.setInt("/Data/"+deviceID + "/Alert", 1);
      }

      else{
          Firebase.setInt("/Data/"+deviceID + "/Alert", 0);
      } 
      //********
  
      
  }
}

String removeWord(String str, String word) {
  int index = str.indexOf(word);
  if (index == -1) return str;
  int len = word.length();
  return str.substring(0, index) + str.substring(index+len);
}
