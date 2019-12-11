import time
import random
import json

import RPi.GPIO as GPIO
import MFRC522

import signal

continue_reading = True

from AWSIoTPythonSDK.MQTTLib import AWSIoTMQTTShadowClient

def end_read(signal, frame):
    global continue_reading
    print "Ctrl+C captured, ending read."
    continue_reading = False
    GPIO.cleanup

signal.signal(signal.SIGINT, end_read)

MIFAREReader = MFRC522.MFRC522()

print "Press Ctrl-C to stop."

Client_ID = "IoT_System_Client_Pub"
Thing_Name = "raspberrypi_2" 				    
Host_Name = "a89fa2yt1cyk2-ats.iot.us-west-2.amazonaws.com" 	    
Root_CA = "/home/pi/Downloads/root-CA.crt" 			
Private_Key = "/home/pi/Downloads/raspberrypi_2.private.key"
Cert_File = "/home/pi/Downloads/raspberrypi_2.cert.pem" 


Client = AWSIoTMQTTShadowClient(Client_ID)
Client.configureEndpoint(Host_Name, 8883)
Client.configureCredentials(Root_CA, Private_Key, Cert_File)
Client.configureConnectDisconnectTimeout(10)
Client.configureMQTTOperationTimeout(5)
Client.connect()

def Callback_func_Update(payload, responseStatus, token):
    print("")
    #print("UPDATE: $aws/things/" + Thing_Name + "/shadow/update/#\n")
    #print("payload = " + payload + "\n")
    #print("responseStatus = " + responseStatus + "\n")
    #print("token = " + token + "\n")
    
Handler = Client.createShadowHandlerWithName(Thing_Name, True)

(status, TagType) = MIFAREReader.MFRC522_Request(MIFAREReader.PICC_REQIDL)

while continue_reading :
        (status, TagType) = MIFAREReader.MFRC522_Request(MIFAREReader.PICC_REQIDL)

        (status, TagType) = MIFAREReader.MFRC522_Request(MIFAREReader.PICC_REQIDL)
        if status == MIFAREReader.MI_OK:
            print ("\n\n===== Detected!!! =====\n\n")

            (status,uid) = MIFAREReader.MFRC522_Anticoll()

            if status == MIFAREReader.MI_OK:
                #print "Card read UID: %s,%s,%s,%s" % (uid[0], uid[1], uid[2], uid[3])
                key = [0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF]
                MIFAREReader.MFRC522_SelectTag(uid)
                status = MIFAREReader.MFRC522_Auth(MIFAREReader.PICC_AUTHENT1A, 8, key, uid)
                if status == MIFAREReader.MI_OK:
                    backData = MIFAREReader.MFRC522_Read(8)
                    msg = {"state": {"desired": {"Reader2": {
                    "Model": str(backData[0]),
                    "Size": str(backData[1])+str(backData[2])+str(backData[3])}}}}
                    Handler.shadowUpdate(json.dumps(msg), Callback_func_Update, 5)

                    MIFAREReader.MFRC522_StopCrypto1()
                
                else:
                    print "Authentication error"
            
            time.sleep(5)
        else:
            print("\n\n===== Not Detected =====\n\n")
            msg = {"state": {"desired": {"Reader2": {
            "Model": "0",
            "Size": "0"}}}}

            Handler.shadowUpdate(json.dumps(msg), Callback_func_Update,5)

            
            time.sleep(5)
            (status, TagType) = MIFAREReader.MFRC522_Request(MIFAREReader.PICC_REQIDL)










'''

def Callback_func_Get(payload, responseStatus, token):
    print(responseStatus)
    msg = json.loads(payload)
    print(str(msg["state"]["desired"]["Model"]))
    print(str(msg["state"]["desired"]["Size"]))


def Callback_func_Delete(payload, responseStatus, token):

    print("Delete\n")


data= {"state": {"desired":{
    "Model" : "1",
    "Size" : "280"}}}
Handler.shadowUpdate(json.dumps(data),Callback_func_Update,5)

#Handler.shadowDelete(Callback_func_Delete, 5)

#Handler.shadowGet(Callback_func_Get,5)

'''


