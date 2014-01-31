#!/usr/bin/env python
__author__ = 'guyhawkins'
"""
A simple echo server
"""

import socket
from time import localtime, strftime


def time():
    return strftime("%a, %d %b %Y %H:%M:%S", localtime())

def log(m):
    t = time()
    print t + ': ' + m


secKey = 'asdfadf'
host = ''
port = 61845
backlog = 5
size = 4096
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((host, port))
s.listen(backlog)
print 'server open on port ' + str(port)
while 1:
    client, address = s.accept()
    data = client.recv(size)
    if data:
        data = data.strip()
        if data == secKey:
            print 'secret message received!'
            client.send('all your base are belong to us!')
        else:
            log(data)
            client.send('message received at ' + time())
    client.close()
print 'server shut down!'

