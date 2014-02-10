#!/usr/bin/env python
__author__ = 'guyhawkins'

import socket
import configuration
import parse
from time import time
from auxFunctions import log, verifyData, parseData





secretKey = configuration.secretKey
#set host = ''
host = configuration.host
port = configuration.port
#5 should be just fine
backlog = configuration.backlog
#use 1024 to start if you're unfamiliar
size = configuration.size

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
trying = True
while trying:
    try:
        s.bind((host, port))
        trying = False
    except socket.error:
        port += 1

s.listen(backlog)

#Connect to Parse
try:
    parse.parse_init()
except:
    print 'Error connecting to Parse'
    exit()

print 'server listening on port ' + str(port)
num = 1
while True:
    print 'Listening for incoming requests...'
    client, address = s.accept()
    print 'incoming request from: ' + str(address)
    fragment = client.recv(size)
    while fragment:
        client.settimeout(configuration.socketTimeout)
        try:
            fragment += client.recv(size)
            fragment_end = fragment.find('\n')
            #our fragment contains \n, we have entire message
            if fragment_end != -1:
                rawData = fragment[:fragment_end]
                #take raw data and make sense of it
                response = parseData(rawData)
                client.send(response)
                #we are done, null out fragment and listen to incoming connections
                fragment = None
        except socket.timeout:
            print 'timeout occurred'
            client.send("timeout")
            fragment = None

    client.close()
print 'server shut down!'

