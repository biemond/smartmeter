Raspberry PI Java Energy Smartmeter application
===============================================

Java Embedded Suite of Oracle Application which reads the energy meter (gas & electricity) and expose these data as restful service which can be used in your mobile applications  
All the data are stored every 30 minutes in the Embedded Derby Database  
and using JAX-RS to expose the data ( accepts JSON ,XML , JSONP with callback) 
           
          
[Used the RJ11/USB P1 Converter Cable](https://sites.google.com/site/nta8130p1smartmeter/webshop)
Raspberry Pi B 512mb version
Tested on a Liander meter ( Netherlands )

Used Soft-float Debian “wheezy” pi image cause the need for Oracle JDK.  
This image is identical to the Raspbian “wheezy” image, but uses the slower soft-float ABI. It is only intended for use with software such as the Oracle JVM which does not yet support the hard-float ABI used by Raspbian.

Update the Operating System  

    sudo apt-get update
    sudo apt-get upgrade

Do a test
---------

Install a terminal program and it should give you an output like this

    sudo apt-get install cu minicom   
    cu -l /dev/ttyUSB0 -s 9600 --parity=none

    /ISk5\2ME382-1003
    
    0-0:96.1.1(4B414C37303035313039373534393132)
    1-0:1.8.1(00659.902*kWh)
    1-0:1.8.2(00399.981*kWh)
    1-0:2.8.1(00121.883*kWh)
    1-0:2.8.2(00246.097*kWh)
    0-0:96.14.0(0002)
    1-0:1.7.0(0000.14*kW)
    1-0:2.7.0(0000.00*kW)
    0-0:17.0.0(0999.00*kW)
    0-0:96.3.10(1)
    0-0:96.13.1()
    0-0:96.13.0()
    0-1:24.1.0(3)
    0-1:96.1.0(3238303131303031323331333231343132)
    0-1:24.3.0(130523190000)(00)(60)(1)(0-1:24.2.1)(m3)
    (01168.041)
    0-1:24.4.0(1)
    !

~. to do an exit

to understand output you can read this implementation document DSMR3.0-final-P1-1.pdf in the docs folder of this repository

DerbyDB DataModel
-----------------

      CREATE TABLE devices
       ( id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) CONSTRAINT DEVICE_PK PRIMARY KEY
       , type 	 VARCHAR(100)  NOT NULL
       , device  VARCHAR(100)	NOT NULL 
       );
      
      CREATE TABLE gas_measurements
       ( id          INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) CONSTRAINT GAS_PK PRIMARY KEY
       , device      INTEGER NOT NULL CONSTRAINT gas_device_fk REFERENCES devices
       , date        DATE NOT NULL
       , time        TIMESTAMP NOT NULL
       , measurement NUMERIC(8,3)
       , enabled     INTEGER
       );

      CREATE TABLE energy_measurements
       ( id       INTEGER   NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) CONSTRAINT ELEC_PK PRIMARY KEY
       , device   INTEGER   NOT NULL CONSTRAINT elec_device_fk REFERENCES devices
       , date     DATE      NOT NULL
       , time     TIMESTAMP NOT NULL
       , meter181 NUMERIC(8,3)
       , meter182 NUMERIC(8,3)
       , meter281 NUMERIC(8,3)
       , meter282 NUMERIC(8,3)
       , tarif    VARCHAR(100)
       , currentConsumption NUMERIC(5,0)
       , currentProduction  NUMERIC(5,0)
       , enabled            INTEGER
       );
       
Oracle Java Embedded Suite and Java SE Embedded
-----------------------------------------------

Download Java SE Embedded and Embedded Suite.

Extra information in [this blogpost](http://adf4beginners.blogspot.nl/2013/04/how-to-get-java-embedded-suite-running.html)  

Java SE Embedded 7 (here). Make sure you download ARMv6/7 Linux - Headless EABI, VFP, SoftFP ABI, Little Endian  

     tar -zxvf *.gz

This should create /home/pi/ejre1.7.0_21

Change your .profile of the pi user  
And add the following lines   

     export JAVA_HOME=/home/pi/ejre1.7.0_21
     export PATH=$JAVA_HOME/bin:$PATH

Test the java 

     pi@raspberrypi ~ $ java -version
     java version "1.7.0_21"
     Java(TM) SE Embedded Runtime Environment (build 1.7.0_21-b11, headless)
     Java HotSpot(TM) Embedded Client VM (build 23.21-b01, mixed mode)

Oracle Java Embedded Suite for 'ARMv6/v7 Linux' 
upload JES and extract this to /home/pi/jes7.0  


[RXTX](http://rxtx.qbang.org/wiki/index.php/Download)  
http://eclipsesource.com/blogs/2012/10/17/serial-communication-in-java-with-raspberry-pi-and-rxtx/  
We will use the RXTX library to do all the serial communication  

   sudo apt-get install librxtx-java

Test the application
--------------------

mkdir /home/pi/smartmeter  
upload startWeb.sh ,jackson-core-asl-1.9.12.jar, jackson-mapper-asl-1.9.12.jar, and the webhost.jar  

Open and build the application in netbeans.

upload the SmartMeter.jar from the netbeans smartapplication dist folder to /home/pi/smartmeter

     cd /home/pi/smartmeter
     ./startWeb.sh
     
     pi@raspberrypi ~/smartmeter $ ./startWeb.sh
     Deploying /home/pi/smartmeter/SmartMeter.jar ...
     static
     MainProcess run
     SmartMeterApplication getClasses
     SmartMeterApplication getClasses
     connection made
     Press <Enter> to exit server.
     return instance
     ProcessData
     connect on usb
     start new thread on Fri May 24 22:44:25 CEST 2013
     return thread
     start new thread on Fri May 24 23:14:25 CEST 2013
     return thread

Start in the background and measure for a week

     cd /home/pi/smartmeter
     nohup ./startWeb.sh >/tmp/nohup.out 2>&1 &
          

Retrieve the restful data ( accepts JSON ,XML , JSONP with callback)
----------------------------------------------------------------------

Open google chrome browser and install and use the advanced rest client plugin.
                                  
all the measured devices                                  
GET http://xxxx:8080/SmartMeter/listDevices
     
    {
       "device":[
          {
             "id":"3",
             "type":"ELEC",
             "device":"4B414C37303035313039373534393132"
          },
          {
             "id":"4",
             "type":"GAS",
             "device":"3238303131303031323331333231343132"
          }
       ]
    }

List all the gas measurements for 2013-05-25         
GET http://192.168.2.10:8080/SmartMeter/listGas/2013-05-25 
    
    {
       "gasMeasurement":[
          {
             "device":{
                "id":"4",
                "type":"GAS",
                "device":"3238303131303031323331333231343132"
             },
             "id":"19",
             "date":"2013-05-25T00:00:00+02:00",
             "time":"2013-05-25T21:00:00+02:00",
             "measurement":"1175.022",
             "enabled":"1"
          },
          {
             "device":{
                "id":"4",
                "type":"GAS",
                "device":"3238303131303031323331333231343132"
             },
             "id":"18",
             "date":"2013-05-25T00:00:00+02:00",
             "time":"2013-05-25T20:00:00+02:00",
             "measurement":"1175.012",
             "enabled":"1"
          }
        ]
    }      
     
List all the energy measurements for 2013-05-24
GET http://192.168.2.10:8080/SmartMeter/listEnergy/2013-05-24
     
    {
       "energyMeasurement":[
          {
             "device":{
                "id":"3",
                "type":"ELEC",
                "device":"4B414C37303035313039373534393132"
             },
             "id":"33",
             "date":"2013-05-24T00:00:00+02:00",
             "time":"2013-05-24T23:47:56.072+02:00",
             "meter181":"661.405",
             "meter182":"402.298",
             "meter281":"121.884",
             "meter282":"250.225",
             "tarif":"0001",
             "currentConsumption":"270.0",
             "currentProduction":"0.0",
             "enabled":"1"
          },
          {
             "device":{
                "id":"3",
                "type":"ELEC",
                "device":"4B414C37303035313039373534393132"
             },
             "id":"32",
             "date":"2013-05-24T00:00:00+02:00",
             "time":"2013-05-24T23:14:31.756+02:00",
             "meter181":"661.203",
             "meter182":"402.298",
             "meter281":"121.884",
             "meter282":"250.225",
             "tarif":"0001",
             "currentConsumption":"350.0",
             "currentProduction":"0.0",
             "enabled":"1"
          }
        ]
    }
    
all the gas overview per day and with differences for device 4 
GET http://192.168.2.10:8080/SmartMeter/listGasOverview/4

    {
       "gasOverview":[
          {
             "device":{
                "id":"4",
                "type":"GAS",
                "device":"3238303131303031323331333231343132"
             },
             "date":"2013-05-25T00:00:00+02:00",
             "consumption":"1174.38",
             "difference":"0"
          },
          {
             "device":{
                "id":"4",
                "type":"GAS",
                "device":"3238303131303031323331333231343132"
             },
             "date":"2013-05-24T00:00:00+02:00",
             "consumption":"1172.0",
             "difference":"2"
          }
       ]
    }
    
all the energy overview per day and with differences for device 3   
GET http://192.168.2.10:8080/SmartMeter/listEnergyOverview/3

    {
       "energyOverview":[
          {
             "date":"2013-05-25T00:00:00+02:00",
             "consumption":"1066.49",
             "consDifference":"0",
             "production":"373.67",
             "prodDifference":"0",
             "device":{
                "id":"3",
                "type":"ELEC",
                "device":"4B414C37303035313039373534393132"
             }
          },
          {
             "date":"2013-05-24T00:00:00+02:00",
             "consumption":"1063.43",
             "consDifference":"3",
             "production":"372.1",
             "prodDifference":"2",
             "device":{
                "id":"3",
                "type":"ELEC",
                "device":"4B414C37303035313039373534393132"
             }
          }
       ]
    }
    
clean the database  
GET http://192.168.2.10:8080/SmartMeter/deleteDatabase      