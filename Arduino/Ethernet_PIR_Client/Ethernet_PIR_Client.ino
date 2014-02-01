#include <SPI.h>
#include <Ethernet.h>

//////////////////////
// ETHERNET members //
//////////////////////
// Enter a MAC address for your controller below.
// Newer Ethernet shields have a MAC address printed on a sticker on the shield
byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
// if you don't want to use DNS (and reduce your sketch size)
// use the numeric IP instead of the name for the server:
IPAddress server(162,243,215,70);
// Set the static IP address to use if the DHCP fails to assign
IPAddress ip(192, 168, 0, 177);
int port = 61845;
// Initialize the Ethernet client library
// with the IP address and port of the server
EthernetClient client;
/////////////////////////////////////////////////////////////////////////////////////
//    PIR VARS  //
//////////////////
//the time we give the sensor to calibrate (10-60 secs according to the datasheet)
int calibrationTime = 30;        
//the time when the sensor outputs a low impulse
long unsigned int lowIn;         
//the amount of milliseconds the sensor has to be low 
//before we assume all motion has stopped
long unsigned int pause = 5000;  
//used to calculate sesnors active time for an event
boolean calmState = true;
boolean takeCalmTime;  
//the digital pin connected to the PIR sensor's output
int pirPin = 2;
/////////////////////////////////////////////////////////////////////////////////////
// misc //
/////////
//check in every 5 mins
unsigned long checkIn = (10000);
unsigned long lastCheck = 0;
int ledPin = 13;
/////////////////////////////////////////////////////////////////////////////////////
//initialization
void setup() {
  // Open serial communications and wait for port to open:
  Serial.begin(9600);
  //led setup
  pinMode(ledPin, OUTPUT);
  //configure ethernet and establish connection with server
  ethernetSetup("Initial Setup");
  //callibrate PIR sensor
  pirSetup();
}
//main loop
void loop()
{
  //delay(1000);
  //read any messages from server
  readBuffer();
  //check in or check sensors
  
  if (millis()%1000 == 0){
  Serial.print("millis: ");
  Serial.println(millis());
  
  Serial.print("lastCheck: ");
  Serial.println(lastCheck);
  
  Serial.println("checkIn: ");
    Serial.println(checkIn);
  
  Serial.print("millis - lastCheck: ");
  Serial.println(millis() - lastCheck);
  Serial.println();
  }
  if(millis() - lastCheck > checkIn){
    lastCheck = millis();
    Serial.println("Checking in with Server");
    connectAndSend("Checking in");
  }
  else{
    //get state of PIR sensor
    pirState();
  }
}
/////////////////////////
// Ethernet Controlers //
/////////////////////////
//call to reboot ethernet connection
void reBoot(String _m){
  ethernetSetup('reBoot: ' + _m);
}
//connect to ethernet
void ethernetSetup(String _m){
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
      client.println(_m);
    }
    else{
      //no good, wait one minute and try again
      Serial.println("connection failed, try again in a minute");      
      delay(60000);
    }
  }
}
//connect with server
//returns status of connection
boolean connect(){ 
 return client.connect(server, port);
}
//connect with server and send message
//return true if message sent
void connectAndSend(String _m){
  if (connect()) {
    client.println(_m);
    Serial.println("message sent: " + _m);
  }
  else{
    Serial.println("connection not available, try reboot");
    reBoot(_m);
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
      Serial.print((millis())/1000);
      Serial.println(" sec"); 
      //send to server
      connectAndSend("Motion Detected");
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
         Serial.print((millis() - pause)/1000);
         Serial.println(" sec");
         //alert server to have moved back to a calm state
         connectAndSend("Motion Deceased");
         delay(50);
      }
    }
}
