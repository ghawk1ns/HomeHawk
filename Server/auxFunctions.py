__author__ = 'guyhawkins'
from time import gmtime, strftime

def time():
    return strftime("%a, %d %b %Y %H:%M:%S", gmtime())

def log(m):
    t = time()
    print t + ': ' + m

def verifyData(ls):
    for x in ls:
        if x != '0' or x != '1':
            return False
    return True

def parseData(bitList):
    return int(''.join(bitList[0:8]),2),int(''.join(bitList[8:16]),2),int(''.join(bitList[16:20]),2)
