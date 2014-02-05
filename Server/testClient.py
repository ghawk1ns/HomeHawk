__author__ = 'guyhawkins'

#!/usr/bin/python           # This is client.py file

import socket               # Import socket module
import configuration

s = socket.socket()
host = socket.gethostname()
port = configuration.port

s.connect((host, port))

m = bytearray(0xAD)
s.send(m)

print s.recv(1024)
s.close                     # Close the socket when done