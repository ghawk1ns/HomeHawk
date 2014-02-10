from parse_rest.user import User

__author__ = 'guyhawkins'
import configuration
import json

from parse_rest.connection import register
from parse_rest.datatypes import Object
#initial registration
def parse_init():
    register(configuration.applicationID, configuration.restAPIKey, master_key=configuration.masterKey)
###########################################
## Header Object ##
###################
class Header(Object):
    pass
#Get the header objects - these contain valid header numbers for message protocol
def getHeader(_id):
    try:
        return Header.Query.get(objectId=_id)
    except:
        return None
###########################################
## Client Object ##
###################
class Client(Object):
    pass
#Get client by sysId else return all client
def getClient(_id=None):
    if _id is not None:
        try:
            return Client.Query.get(objectId=_id)
        except:
            return None
    else:
        return Client.Query.all()

###########################################
## Sensor Object ##
###################
class Sensor(Object):
    def isActive(self,_isActive):
        self.isActive = _isActive
        self.save()
    pass

def getSensor(_id=None):
    if _id is not None:
        try:
            return Sensor.Query.get(objectId=_id)
        except:
            return None
    else:
        return Sensor.Query.all()
###########################################
## Client-Sensor Pairs ##
#########################
class Pair_Client_Sensor(Object):
    pass

def addClientSensorPair(_c,_s):
    Pair_Client_Sensor(client=_c.getObjectId, sensor=_s.getObjectId).save()

###########################################
## User-Client Pairs ##
#######################
class Pair_User_Client(Object):
    pass


###########################################
## User Functions ##
####################
'''
create an account
'''
def createAccount(_u,_pw,_em=None):
    try:
        u = User.signup(_u, _pw, email=_em)
    except Exception, e:
        try:
            data = json.loads(e.message)
        except:
            return None
        if 'error' in data:
            print data['error']
            return None
    return u
'''
Log in to account
'''
def loginAccount(_u,_p):
    try:
        u = User.login(_u,_p)
    except Exception, e:
        try:
            data = json.loads(e.message)
        except:
            return None
        if 'error' in data:
            print data['error']
            return None
    return u
