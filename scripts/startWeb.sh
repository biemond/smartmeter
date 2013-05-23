#!/bin/bash

# Copyright (c) 2012 Oracle and/or its affiliates.
# All rights reserved. Use is subject to license terms.
#
cd /home/pi/jes7.0/samples/dist/run

JES_HOME=/home/pi/jes7.0

if [ -z "$JES_WORKDIR" ] ; then
    JES_WORKDIR=`pwd`/workdir
fi
if [ -e "$JES_WORKDIR" ] ; then
    rm -fr $JES_WORKDIR
fi
mkdir $JES_WORKDIR

SMARTMETER_CLASSPATH="/usr/share/java/RXTXcomm.jar:/home/pi/smartmeter/SmartMeter.jar"

JES_GLASSFISH_CLASSPATH="$JES_HOME/glassfish/lib/glassfish-jes.jar"
JES_JAVADB_CLASSPATH="$JES_HOME/javadb/lib/derby.jar:$JES_HOME/javadb/lib/derbytools.jar"
JES_JERSEY_CLIENT_CLASSPATH="$JES_HOME/jersey/lib/jersey-core.jar:$JES_HOME/jersey/lib/jersey-client.jar:$JES_HOME/jersey/lib/jersey-json.jar"
JES_JERSEY_SERVER_CLASSPATH="$JES_HOME/jersey/lib/jersey-core.jar:$JES_HOME/jersey/lib/jersey-server.jar:$JES_HOME/jersey/lib/jersey-json.jar"
JES_JERSEY_SERVLET_CLASSPATH="$JES_JERSEY_SERVER_CLASSPATH:$JES_HOME/jersey/lib/jersey-servlet.jar"
JES_JERSEY_CLASSPATH="$JES_HOME/jersey/lib/asm.jar:$JES_JERSEY_SERVER_CLASSPATH"

JES_CLASSPATH="$SMARTMETER_CLASSPATH:$JES_JERSEY_CLASSPATH:$JES_GLASSFISH_CLASSPATH:$JES_JERSEY_SERVLET_CLASSPATH:$JES_JAVADB_CLASSPATH:/home/pi/jes7.0/samples/dist/webhost.jar"

java -Xmx128m -Djava.library.path=/usr/lib/jni -classpath $JES_CLASSPATH com.oracle.jes.util.webhost.LightweightHost /home/pi/smartmeter/SmartMeter.jar nl.biemond.smartmeter.SmartMeterApplication