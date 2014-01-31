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
// that you want to connect to (port 80 is default for HTTP):
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
// led //
/////////
int ledPin = 13;
/////////////////////////////////////////////////////////////////////////////////////
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
//main loop
void loop()
{
  //delay(1000);
  //read any messages from server
  readBuffer();
  
  //get state of PIR sensor
  pirState();
  
  // if the server's disconnected, stop the client:
//  if (!client.connected()) {
//    Serial.println();
//    Serial.println("disconnecting.");
//    client.stop();
//  }
}

/////////////////////////
// Ethernet Controlers //
/////////////////////////
void ethernetSetup(){
  // start the Ethernet connection:
  if (Ethernet.begin(mac) == 0) {
    Serial.println("Failed to configure Ethernet using DHCP");
    // no point in carrying on, so do nothing forevermore:
    // try to congifure using IP address instead of DHCP:
    Ethernet.begin(mac, ip);
  }
  // give the Ethernet shield a second to initialize:
  delay(1000);
  Serial.println("connecting...");

  // if you get a connection, report back via serial:
  if (connect()) {
    Serial.println("connected");
    client.println("secClient starting");
  }
  else {
    // kf you didn't get a connection to the server:
    Serial.println("connection failed");
  }
}
//connect with server
//returns status of connection
boolean connect(){ 
 return client.connect(server, port);
}
//connect with server and send message
//return true if message sent
boolean connectAndSend(String _m){
  if (connect()) {
    Serial.println("connecting...");
    client.println(_m);
    Serial.println("message sent");
    return true;
  }
  else{
    Serial.println("connect not available");
    return false;
  }
}

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
 
boolean pirState(){
  
  boolean activity = false;
  
  if(digitalRead(pirPin) == HIGH){
    activity = true;
    digitalWrite(ledPin, HIGH);   //the led visualizes the sensors output pin state
    if(calmState){
      //send a message to the server that we have moved from calm state
      connectAndSend("Motion Detected");
      //makes sure we wait for a transition to LOW before any further output is made:
      calmState = false;            
      Serial.println("---");
      Serial.print("motion detected at ");
      Serial.print((millis())/1000);
      Serial.println(" sec"); 
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
         //alert server to have moved back to a calm state
         connectAndSend("Motion done- state now calm");
         //makes sure this block of code is only executed again after 
         //a new motion sequence has been detected
         calmState = true;                        
         Serial.print("motion ended at ");      //output
         Serial.print((millis() - pause)/1000);
         Serial.println(" sec");
         delay(50);
      }
    }
    
    return activity;
}
