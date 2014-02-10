__author__ = 'guyhawkins'

#!/usr/bin/python           # This is client.py file

import socket               # Import socket module
import configuration

s = socket.socket()
host = socket.gethostname()
port = 61846

s.connect((host, port))

m = '{ "clientId":"Arduino yo","sensorId":"PIR Sensor","sensorData": { "isActive": 1 ,"time":"2:31"} }/n'
s.send(m)

print s.recv(1024)
# Close the socket when done
s.close