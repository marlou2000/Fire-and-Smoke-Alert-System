#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>  
#include <FirebaseArduino.h>  

#define FIREBASE_HOST "smoke-and-fire-alert-system-default-rtdb.firebaseio.com"      
#define FIREBASE_AUTH "hcWiOLb0fV9njyTVUwYtHSWnu5piF0Wmxfc1rrGa"

#define WIFI_SSID "Galaxy A01 Core9067"                                  
#define WIFI_PASSWORD "samsung01"   
//#define WIFI_SSID "NDMUWLAN1"                                  
//#define WIFI_PASSWORD ""   

HTTPClient http;

int comma;
String result;
String copyOfResponse;

void setup() {
  Serial.begin(115200); 

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

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);                 // connect to the firebase
}

void loop() {
  int counterLoop = 0;
  int numberOfAlert = 0;
  
  http.begin("http://fireandsmoke.000webhostapp.com/index.php");
  
  int httpCode = http.GET();

  //this will check if we get the response or not
  if (httpCode == HTTP_CODE_OK) {

    String response = http.getString(); // this is the response from API
    copyOfResponse = response; // copy of response to be used for concatination
    
    //for getting the amount of Device ID 
    for(;;){
          comma = response.indexOf(","); //get the string until comma
          result = response.substring(0, comma); //Get the first deviceID
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
        
        comma = copyOfResponse.indexOf(","); //get the string until comma
        result = copyOfResponse.substring(0, comma); //Get the first deviceID
        int lengthOfResult = strlen(result.c_str()); //Get the length of the id
        int lengthOfResultFinal = lengthOfResult + 1; //Get the length of the id + 1
        int lengthOfResponse = strlen(copyOfResponse.c_str()); //Get the total length of the response
        copyOfResponse = copyOfResponse.substring(lengthOfResultFinal, lengthOfResponse); //getting the new response withouth the first ID 
        
        deviceIDArray[i] = result;
    }

    //For checking if there is alert or not
    for(int z = 0; z < counterLoop; z++){
        int alert = Firebase.getInt("Data/"+ deviceIDArray[z]+"/Alert");
        String area = Firebase.getString("/Data/"+ deviceIDArray[z]+"/Area");
          
        if(alert == 1){
          numberOfAlert++;
        }
    }
    //*****


    //check if need to turn off all the relays
    if(numberOfAlert > 1){
      Firebase.setInt("Alert", 1);
    }

    else{
      Firebase.setInt("Alert", 0);
    }
    //**************
    
  } 
  
  else {
    Serial.println("Error: " + String(httpCode));
  }

  // Close the connection
  http.end();
}
