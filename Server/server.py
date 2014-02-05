#!/usr/bin/env python
__author__ = 'guyhawkins'

import socket
import configuration
from auxFunctions import time, log, verifyData, parseData

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
        #data = data.strip()
        if data == secretKey:
            print 'secret message received!'
            client.send('all your base are belong to us!')
        else:
            bitList = [str(ord(b)) for b in data]
            if len(bitList) == 32 and verifyData(bitList):
                print str(bitList)
                header, clientId, sensorId = parseData(bitList)
                print 'header: ' + str(header)
                print "client id: " + str(clientId)
                print 'sensor id: ' + str(sensorId)
                print 'message: ' + str(bitList[20:])
                print '\n\n'
                client.send('Thank you')
            else:
                client.send('error, invalid size')
            #log(int(data))

    client.close()
print 'server shut down!'

