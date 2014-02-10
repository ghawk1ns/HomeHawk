#include <SPI.h>
#include <Ethernet.h>
//////////////////////
// ETHERNET members //
//////////////////////
// controller's MAC address
byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
// use the numeric IP instead of the name for the server:
//IPAddress server(162,243,215,70);
IPAddress server(10,0,0,7);
// Set the static IP address to use if the DHCP fails to assign
IPAddress ip(192, 168, 0, 177);
int port = 61846;
EthernetClient client;
////////////////////////////////////////////////////////////
String sensorA = "f0Y4dakg71";
String sensorB = "98df9ak8sd";
String deviceId = "RvMQuSPEDk";
String headerId = "VEo6dnPeff";

String checkInJSON(){
  return "{\"headerId\":\""+headerId+"\",\"deviceId\":\""+deviceId+"\",\"checkIn\":1}";
}
//append a sensorId and json for that sensors data to message
//String m = "{ \"clientId\":\"Arduino yo\",\"sensorId\":\"PIR Sensor\",\"sensorData\": { \"isActive\": 1 ,\"time\":\"2:31\"} }";
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void setup() {
  Serial.begin(9600);
   ethernetSetup();
   delay(5000);
}
////////////////////////////////////////////////////////////////////////////////
void loop()
{
  
  delay(15000);  
}
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
//connect to ethernet
boolean connect(){ 
 return client.connect(server, port);
}
//intial setup
void ethernetSetup(){
  boolean complete = false;
  while (!complete){
     // start the Ethernet connection:
    if (Ethernet.begin(mac) == 0) {
      Serial.println("Failed to configure Ethernet using DHCP");
      // no point in carrying on, so do nothing forevermore:
      // try to congifure using IP address instead of DHCP:
      Ethernet.begin(mac, ip);
    }
    // give the Ethernet shield a sec to initialize:
    delay(1500);
    complete = connect();
    if(complete){
      String msg = checkInJSON();
      Serial.println("sending: "+ msg);
      client.println(msg);
      client.disconnect();
    }
    else{
      //no good, wait one minute and try again
      Serial.println("connection failed, try again in a minute");      
      //delay(60000);
      delay(1000);
    }
  }
}

