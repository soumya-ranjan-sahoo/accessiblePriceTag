#include <CapacitiveSensor.h>
#include "ESP8266WiFi.h"
 #include <ESP8266HTTPClient.h>
  
const int numReadings = 4;
const int val = 1000; //  Total number of values read by sensor
int readings[val];      // the readings from the  input
int readIndex = 0;              // the index of the current reading
int total = 0;                  // the running total
int average = 0 ; // To take average of 4 sensor values at a time
int count = 0 ; // counter to check the number of values read by the sensor

// touch sensor config
CapacitiveSensor capSensor1 = CapacitiveSensor(D4, D2);

// WiFi parameters
const char* ssid = "pi";
const char* password = "unisaarland";

const char* host = "192.168.5.1"; // Pi server IP
long sensorValue1; // Variable for reading sensor values
WiFiClient client;

void setup() 
{
  Serial.begin(115200);
   
 // Connect to WiFi
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) 
  {
    delay(500);
    Serial.print(".");
  }
Serial.println("");
Serial.println("WiFi connected");
Serial.println(WiFi.localIP());

// initialising the reading variable 
for (int thisReading = 0; thisReading < val; thisReading++)
{
    readings[thisReading] = 0;
}
}

void loop()
 {
  // read values  from the sensor:
  long start = millis();
  long sensorValue1 = capSensor1.capacitiveSensor(30);
  readings[readIndex] = sensorValue1;
    
  // add the reading to the total:
  total = total + readings[readIndex];
  // advance to the next position in the array:
  readIndex = readIndex + 1;
  count = count +1; 

  // Here we are checking if we got 4 sensor values and wifi status connected 

 if(WiFi.status()== WL_CONNECTED && count ==4)
  {   //Check WiFi connection status
   
       Serial.println("host : 192.168.5.1");
       Serial.println("connected");
   
        // taking average of 4 sensor values
       average = total / numReadings;
       Serial.println(average);
   
        // Here we are only sending the average of 4 sensor values   
        String url = "/sensor?command=sensor&value=";
        url += average;
        url += "&id=";    // Variable for detecting from which wemos, touch input is coming
        url += "wemos2";
   
        // Here we are checking if client is  also connected
       if(client.connect("192.168.5.1", 3000))
         {
            Serial.println("client");
           // Sending only those touch values whose average is greater than 15 
                if(average > 15)
                {
                    client.print(String("GET ") + url + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" + 
                    "Connection: close\r\n\r\n");
                }
         }
  // reinitialising count, total, average     
  count = 0;
  total = 0;
  average = 0;
 }
 Serial.print("\t");  
 Serial.println(sensorValue1);
  delay(250);  
 }
