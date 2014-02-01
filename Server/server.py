#!/usr/bin/env python
__author__ = 'guyhawkins'
"""
A simple echo server
"""

import socket
import configuration
from auxFunctions import time, log

#
secretKey = configuration.secretKey
#set host = ''
host = configuration.host
port = configuration.port
#5 should be just fine
backlog = configuration.backlog
#use 1024 to start if you're unfamiliar
size = configuration.size
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((host, port))
s.listen(backlog)
print 'server open on port ' + str(port)
while 1:
    client, address = s.accept()
    data = client.recv(size)
    if data:
        data = data.strip()
        if data == secretKey:
            print 'secret message received!'
            client.send('all your base are belong to us!')
        else:
            for x in data:
                print x
            #log(data)
            client.send('message received at ' + time())
    client.close()
print 'server shut down!'

