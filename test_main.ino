#include <CapacitiveSensor.h>
#include "ESP8266WiFi.h"
  #include <ESP8266HTTPClient.h>
// touch sensor config
CapacitiveSensor capSensor1 = CapacitiveSensor(D4, D2);


// WiFi parameters
const char* ssid = "pi";
const char* password = "unisaarland";

const char* host = "192.168.137.227";
long sensorValue1;
WiFiClient client;
void setup() 
{
  Serial.begin(115200);
 
  // Connect to WiFi
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
   Serial.println(WiFi.localIP());

 
}

void loop()
 {
  
    long start = millis();
     sensorValue1 = capSensor1.capacitiveSensor(30);
    
    Serial.print("\t");
    
    Serial.println(sensorValue1);
  
  delay(500);
  
 if(WiFi.status()== WL_CONNECTED)
 {   //Check WiFi connection status
 
 if(client.connect("192.168.137.227", 3000))
 {

Serial.println("host : 192.168.137.184");
Serial.println("connected");
  }
 
 
 String url = "/sensor?command=sensor&value=";
    url += (sensorValue1);
    url += "&ip=";
    url += WiFi.localIP().toString();
    
if(sensorValue1 > 15)
{
    client.print(String("GET ") + url + " HTTP/1.1\r\n" +
               "Host: " + host + "\r\n" + 
               "Connection: close\r\n\r\n");

}
 
 
  


 }
  
}
