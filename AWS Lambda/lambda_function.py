import json
import random
import time
import boto3
from AWSIoTPythonSDK.MQTTLib import AWSIoTMQTTShadowClient


def Callback_func_Get(payload, responseStatus, token):
    print("Get")
    print(responseStatus)
    msg = json.loads(payload)
    #print(str(msg))
    model=[]
    size=[]
    
    model.append(str(msg["state"]["desired"]["Reader1"]["Model"]))
    model.append(str(msg["state"]["desired"]["Reader2"]["Model"]))
    size.append(str(msg["state"]["desired"]["Reader1"]["Size"]))
    size.append(str(msg["state"]["desired"]["Reader2"]["Size"]))
    
    print(model[0])
    print(size[0])
    
    print(model[1])
    print(size[1])
    
    #S3 
    s3 = boto3.client('s3')
    
    # s3.create_bucket(Bucket='123123141241241312414', CreateBucketConfiguration={'LocationConstraint': 'us-west-2'})
    bucket_name = 'lambdas3test4iot'
    
    s3.download_file(bucket_name, 'shoes.txt','/tmp/shoes.txt' )
    
    f=open('/tmp/shoes.txt', 'rt')
    data = f.readlines()
    
    f.close()
    data[6]='0\r\n'    ## 1 280
    data[16]='0\r\n'   ## 2 2
    
    cnt = 10 # item info num
    for j in range(0,2):
        for i in range(0,len(data),cnt):
            if data[2+i]==str(model[j]+'\r\n') and data[3+i]==str(size[j]+'\r\n') :
                print("Reader",j+1," detected")
                data[6+i]='1\r\n'
                break;


    # and write everything back
    f=open('/tmp/shoes.txt', 'wt')
    f.writelines( data )
    
    f.close()
    
    s3.upload_file('/tmp/shoes.txt', bucket_name, 'shoes.txt')
    

def Callback_func_Update(payload, responseStatus, token):
    print("Update")
    print(responseStatus)



Client_ID = "raspberrypi_lambda"
Thing_Name = "raspberrypi_2"
Host_Name = "a89fa2yt1cyk2-ats.iot.us-west-2.amazonaws.com"#End Point
Root_CA = "/var/task/root-CA.crt"
Private_Key = "/var/task/249263565b-private.pem.key"
Cert_File = "/var/task/249263565b-certificate.pem.crt"


Client = AWSIoTMQTTShadowClient(Client_ID)
Client.configureEndpoint(Host_Name, 8883)
Client.configureCredentials(Root_CA, Private_Key, Cert_File)
Client.configureConnectDisconnectTimeout(10)
Client.configureMQTTOperationTimeout(5)
Client.connect()

#Handler = Client.createShadowHandlerWithName(Thing_Name, True)

'''
Handler = Client.createShadowHandlerWithName(Thing_Name, True)
m = {"state": {"reported": {"connecting" : None }}}
Handler.shadowUpdate(json.dumps(m), Callback_func_Update,5)
'''

'''
Handler = Client.createShadowHandlerWithName(Thing_Name, True)
m = {"state": {"reported": {"connecting" : "0" }}}
Handler.shadowUpdate(json.dumps(m), Callback_func_Update,5)
'''




def lambda_handler(event, context):
    # TODO implement
    Handler = Client.createShadowHandlerWithName(Thing_Name, True)
    Handler.shadowGet(Callback_func_Get,5)#Get
    print('abcd')
    time.sleep(1)
    return {
        'statusCode': 200,
        'body': json.dumps('Hello from Lambda!')
}