#include <CapacitiveSensor.h>

// touch sensor config
CapacitiveSensor capSensor1 = CapacitiveSensor(D4, D2);

void setup() 
{
  Serial.begin(115200);
}

void loop()
 {
  
    long start = millis();
    long sensorValue1 = capSensor1.capacitiveSensor(30);
    Serial.print(millis());
    Serial.print("\t");
    
    Serial.print(start);
    Serial.print("\t");
    
    Serial.println(sensorValue1);
  
  delay(500);
}
