__author__ = 'guyhawkins'

#!/usr/bin/python           # This is client.py file

import socket               # Import socket module
import configuration

s = socket.socket()
host = socket.gethostname()
port = 61846

s.connect((host, port))

m = '{ "clientId":"Arduino yo","sensorId":"PIR Sensor","sensorData": { "isActive": 1 ,"time":"2:31"} }\n'
s.send(m)

print s.recv(1024)
size = 1024

# fragment = s.recv(size)
# while fragment:
#     s.settimeout(configuration.socketTimeout)
#     try:
#         fragment += s.recv(size)
#         fragment_end = fragment.find('\n')
#         #our fragment contains \n, we have entire message
#         if fragment_end != -1:
#             rawData = fragment[:fragment_end]
#             #take raw data and make sense of it
#             print rawData
#             #we are done, null out fragment and listen to incoming connections
#             fragment = None
#     except socket.timeout:
#         print 'timeout occurred'
#         fragment = None

s.close