import json
import configuration
import parse
__author__ = 'guyhawkins'
from time import gmtime, strftime


'''
returns the current time formatted
'''
def timefmt():
    return strftime("%a, %d %b %Y %H:%M:%S", gmtime())
'''
print message with time appended to it
'''
def log(m):
    t = timefmt()
    print t + ': ' + m

'''
verify that ls is a list containing only '1' or '0'
'''
def verifyData(ls):
    for x in ls:
        if x != '0' or x != '1':
            return False
    return True
'''
Makse sense of packet from client
'''
def parseData(rawData):
    #first thing, is this a valid json?
    try:
        inputMap = json.loads(rawData)
    except:
        print 'client sent invalid format: ' + str(rawData)
        return 'invalid format'
    ###########################
    ## Message Protocol vars ##
    ###########################
    hId = u'headerId'        ##
    cId = u'clientId'        ##
    sId = u'sensorId'        ##
    sdId = u'sensorData'     ##
    checkIn = u'checkIn'     ##
    sdActive = u'isActive'   ##
    ###########################
    #do hId and cId exist in our map?
    if hId in inputMap and cId in inputMap:
        headerId = inputMap[hId]
        clientId = inputMap[cId]

        clientDevice = parse.getClient(clientId)
        if clientDevice:
            #Is the device checking in?
            if checkIn in inputMap and inputMap[checkIn]:
                #TODO: update parse logs
                print headerId+'-'+clientId+' checking in'
                return 'Thank you for checking in!'
            else:
                if sId in inputMap:
                    sensorId = inputMap[sId]
                    #TODO: validate sensor in parse
                    sensor = parse.getSensor(sensorId)
                    if sensor:
                        if sdId in inputMap:
                            sensorData = inputMap[sdId]
                            if sdActive in sensorData:
                                if sensorData[sdActive]:
                                    #TODO: Notify subscribers
                                    sensor.isActive = True
                                    print headerId + '-' + clientId + '-' + sensorId + ' is ACTIVE'
                                    retMsg = 'ACK: Active'
                                else:
                                    sensor.isActive = False
                                    print headerId + '-' + clientId + '-' + sensorId + ' is INACTIVE'
                                    retMsg = 'ACK: Inactive'
                                sensor.save()
                                return retMsg
                        else:
                            print 'invalid sensor data' + str(inputMap)
                            return 'invalid sensor data'
                    else:
                        print 'invalid sensor Id' + str(inputMap)
                        return 'sensor not recognized'
                else:

        else:
            print 'unrecognized header/device: ' + str(inputMap)
            return 'header/device not recognized'
    else:
        print 'Invalid message: ' + str(inputMap)
        return 'No headerId or No clientId'