#include <ESP8266WiFi.h>                 
#include <FirebaseArduino.h>         
#include <ESP8266HTTPClient.h>
#include <ArduinoJson.h>

#define FIREBASE_HOST "smoke-and-fire-alert-system-default-rtdb.firebaseio.com"      
#define FIREBASE_AUTH "hcWiOLb0fV9njyTVUwYtHSWnu5piF0Wmxfc1rrGa"            
//#define WIFI_SSID "NDMUWLAN1"                                  
//#define WIFI_PASSWORD "samsung01" 
#define WIFI_SSID "NDMUWLAN1"                                  
#define WIFI_PASSWORD ""             

String deviceID = "20191240";

String receivedString;

int loopCounter = 0;

void setup() 
{
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
  Serial.println(WiFi.localIP());                               //prints local IP address
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);                 // connect to the firebase
}

void loop() 
{
 HTTPClient http;
  String url = String(FIREBASE_HOST) + "/Data/Alerts.json?auth=" + FIREBASE_AUTH;
  http.begin(url);
  int httpCode = http.GET();
  
  if (httpCode > 0) {
    String response = http.getString();
    StaticJsonDocument<200> doc;
    DeserializationError error = deserializeJson(doc, response);
    if (error) {
      Serial.println("Error: " + String(error.c_str()));
    } else {
      for (auto& p : doc.as<JsonObject>()) {
        Serial.println(p.key().c_str());
      }
    }
  } else {
    Serial.println("Error: " + String(httpCode));
  }
  http.end();
  delay(3000);
}
