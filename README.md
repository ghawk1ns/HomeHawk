HomeHawk
========

Home Hawk is a home monitoring solution that instantly notifies you when someone is in your home

#######################
## Message Protocol  ##
#######################

Initially I propose a 32 bit message, of course, there are limitations with this but I wanted to keep things simple at first. If your system is larger, message length can be increased to dedicate more bits to a specific section.

TODO: allocate bits for checksum?

TODO: 128-bit message proposal

32 Bit Message:
 ----
[ Header | Client Id | Sensor Id | Sensor Message ]

[ 8 bits | 8 bits | 4 bits | 12 bits ]

[0,1,2,3,4,5,6,7 | 8,9,10,11,12,13,14,15 | 16,17,18,19 | 20,21,22,23,24,25,26,27,28,29,30,31]
 ----

Header: 

    1. Header | [0-7] : unique to each system to validate authenticity (static among users)

    Notes: 
        - Maybe not needed / not best way to validate authenticity
 ---
Client-Id:

    1. Client Id | [8-15] : Allows up to 256 unique devices per system
    2. Sensor Id | [16-19] : Unique Id for a sensor - can dictate how to interpret message
    
    Notes: 
        - Assuming all 0s or all 1s is not valid, 254 possible users can register 14 devices with 14 possible sensors each (devices can have more than one id if it has more than 14 sensors)
        - 
 ---

    Initial idea:

    20: 1 = Sensors state changed
    21: 1 = sensor activated | 0 = sensor deactivated
    22-31: Flags unique to each sensor on system

    Notes: 
        - Message will vary depending on sensor ID, different sensors send different messages
        - All zero message is a check in / sensor test
        - bits 22-31 could be data for ranges/timestamps/etc 
        - We probably need to allocate more bits for message
        
  ---

###############
## Database  ##
###############
Uses Parse for a backend- easily store/retrieve data and push notifications to users on the system
  ---
User: All users on system capable of recieving notifications
  ---
Header: single number (0-255) that clients send to validate authenticity (not to be changed)

    header- int
  ---
Client: Any device capable to sending sensor data to server

    id: int (0-255) unique to system
    name: String
  ---
Sensor:

    id: int (0-15) unique to client
  ---
User-Client:

    pair : relation between a User and Client objects
  ---
Client-Sensor:

    pair : relation between a CLient and Sensor objects
  ---



