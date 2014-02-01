__author__ = 'guyhawkins'
from time import gmtime, strftime

def time():
    return strftime("%a, %d %b %Y %H:%M:%S", gmtime())

def log(m):
    t = time()
    print t + ': ' + m
