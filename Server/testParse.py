from parse_rest.datatypes import Relation

__author__ = 'guyhawkins'

import configuration
import parse



try:
    parse.parse_init()
except:
    print 'Error connecting to Parse'
    exit()

header = 'VEo6dnPeff'
client = 'RvMQuSPEDk'
sensor = 'f0Y4dakg71'

c = parse.getClient(client)
h = parse.getHeader(header)
s = parse.getSensor(sensor)


s.save()

