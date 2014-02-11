#include <SPI.h>
#include <Ethernet.h>

//////////////////////
// ETHERNET members //
//////////////////////
// Enter a MAC address for your controller below.
// Newer Ethernet shields have a MAC address printed on a sticker on the shield
byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
// use the numeric IP instead of the name for the server:
//IPAddress server(162,243,215,70);
IPAddress server(10,0,0,7);
// Set the static IP address to use if the DHCP fails to assign
IPAddress ip(192, 168, 0, 177);
int port = 61846;
// Initialize the Ethernet client library
// with the IP address and port of the server
EthernetClient client;
//number of tries we allow before resetting device
int maxReBootTries = 3;
/////////////////////////////////////////////////////////////////////////////////////
//    PIR VARS  //
//////////////////
//the time we give the sensor to calibrate (10-60 secs according to the datasheet)
int calibrationTime = 30;        
//the time when the sensor outputs a low impulse
long unsigned int lowIn;         
//the amount of milliseconds the sensor has to be low 
//before we assume all motion has stopped
long unsigned int pause = 10000;  
//used to calculate sesnors active time for an event
boolean calmState = true;
boolean takeCalmTime;  
//the digital pin connected to the PIR sensor's output
int pirPin = 2;
/////////////////////////////////////////////////////////////////////////////////////
// client info //
/////////////////
//check in every 5 mins
unsigned long checkIn = (300000);
unsigned long lastCheck = 0;
int ledPin = 13;
/////////////////////////////////////////////////////////////////////////////////////
// Message Protocol //
//////////////////////
String sensorId = "f0Y4dakg71";
String clientId = "RvMQuSPEDk";
String headerId = "VEo6dnPeff";

String checkInJSON(){
  return "{'headerId':'"+headerId+"','clientId':'"+clientId+"','checkIn':True}";
}

String sensorStateChangeJSON(boolean _isActive, String time){
  if(_isActive){
    return "{'headerId':'"+headerId+"','clientId':'"+clientId+"','sensorId':'"+sensorId+"','sensorData':{'isActive':true, time:'"+time+"'}}";
  }
    return "{'headerId':'"+headerId+"','clientId':'"+clientId+"','sensorId':'"+sensorId+"','sensorData':{'isActive':false, time:'"+time+"'}}";
}
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
//for resetting if we can never recover from failed connection
void(* resetFunc) (void) = 0; //declare reset function @ address 0
//initialization
void setup() {
  // Open serial communications and wait for port to open:
  Serial.begin(9600);
  //led setup
  pinMode(ledPin, OUTPUT);
  //configure ethernet and establish connection with server
  ethernetSetup();
  //callibrate PIR sensor
  pirSetup();
}
////////////////////////////////////////////////////////////////////////////////
void loop()
{
  //delay(1000);
  //read any messages from server
  readBuffer();
  //overflow check
  if(millis() < lastCheck){
    lastCheck = 0;
  }
  //check in or check sensors
  if(millis() - lastCheck > checkIn){
    lastCheck = millis();
    checkInWithServer();
  }
  else{
    //get state of PIR sensor
    pirState();
  }
}
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
// Ethernet Controlers //
/////////////////////////
//call to reboot ethernet connection
void reBoot(){
  //set flag to notify reboot occured?
  Serial.println("connection failed, trying to reboot connection");
  client.stop();
  ethernetSetup();
}
//connect to ethernet
void ethernetSetup(){
  boolean complete = false;
  int numTries = 0;
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
      client.println(msg);
      Serial.println("Established connection with server");      
    }
    else{
      numTries++;
      //if we fail to reset 
      if(numTries > maxReBootTries){
        Serial.println("Failed to establish connection, resetting device");
        resetFunc(); 
      } 
      //no good, wait one minute and try again
      Serial.println("connection failed, try again in a minute");      
      delay(60000);
    }
  }
}
//connect with server
//returns status of connection
boolean connect(){ 
 client.stop();
 boolean isConnected = client.connect(server, port);
 if(isConnected){ 
   delay(100);
 }
 return isConnected;
}
//notify the state of the sensor has changed
void checkInWithServer(){
  String msg = checkInJSON();
  if (connect()) {
    delay(50);
    client.println(msg);
    Serial.println("Checking in with server...");
  }
  else{
    reBoot();
  }
}

void notifyStateChange(boolean _isActive, String time){
  delay(1500);
  if (connect()) {
    delay(50);
    String msg = sensorStateChangeJSON(_isActive,time);
    client.println(msg);
    Serial.println("Sent: " + msg);
  }
  else{
    reBoot();
  }
}

//reads incoming messages from server then disconects 
void readBuffer(){
  delay(50);
  if (client.available()) {
    String message = "";
    int c = client.read();
    while(c != -1){
       message += (char) c;
       c = client.read();
    }
    Serial.println("message: " + message);
    Serial.println("disconnecting.");
    Serial.println();
    client.stop();
  }
}
//////////////////////////////////////////////////////////////
// PIR Controlers //
////////////////////
boolean pirSetup(){
  pinMode(pirPin, INPUT);
  digitalWrite(pirPin, LOW);
  //give the sensor some time to calibrate
  Serial.print("calibrating sensor ");
    for(int i = 0; i < calibrationTime; i++){
      Serial.print(".");
      delay(1000);
      }
    Serial.println(" done");
    Serial.println("SENSOR ACTIVE");
    delay(50);
 }
 
void pirState(){
  //   if pirPin is HIGH we have activity 
  if(digitalRead(pirPin) == HIGH){
    digitalWrite(ledPin, HIGH);   //the led visualizes the sensors output pin state
    if(calmState){
      //makes sure we wait for a transition to LOW before any further output is made:
      calmState = false;
      //send a message to the server that we have moved from calm state
      Serial.print("motion detected at ");
      String sec = (String)((millis())/1000);
      Serial.print(sec);
      Serial.println(" sec"); 
      //send to server
      notifyStateChange(true,sec);
      //we sent a message so reset the check in time
      lastCheck = millis();
      delay(50);
    }         
    takeCalmTime = true;
  }
  
  if(digitalRead(pirPin) == LOW){
    digitalWrite(ledPin, LOW);  //the led visualizes the sensors output pin state    
     if(takeCalmTime){
          lowIn = millis();          //save the time of the transition from high to LOW
          takeCalmTime = false;       //make sure this is only done at the start of a LOW phase
      }
     //if the sensor is low for more than the given p ause, 
     //we assume that no more motion is going to happen
     if(!calmState && millis() - lowIn > pause){  
         //makes sure this block of code is only executed again after 
         //a new motion sequence has been detected
         calmState = true;                        
         Serial.print("motion ended at ");      //output
         String sec = (String) ((millis() - pause)/1000);
         Serial.print(sec);
         Serial.println(" sec");
         //alert server to have moved back to a calm state
         notifyStateChange(false,sec);
         delay(50);
      }
    }
}
