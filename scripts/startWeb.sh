#!/bin/bash

# Copyright (c) 2012 Oracle and/or its affiliates.
# All rights reserved. Use is subject to license terms.
#

JES_HOME=/home/pi/jes7.0

SMARTMETER_CLASSPATH="/usr/share/java/RXTXcomm.jar:SmartMeter.jar:webhost.jar"

JES_GLASSFISH_CLASSPATH="$JES_HOME/glassfish/lib/glassfish-jes.jar"
JES_JAVADB_CLASSPATH="$JES_HOME/javadb/lib/derby.jar:$JES_HOME/javadb/lib/derbytools.jar"
JES_JERSEY_CLIENT_CLASSPATH="$JES_HOME/jersey/lib/jersey-core.jar:$JES_HOME/jersey/lib/jersey-client.jar:$JES_HOME/jersey/lib/jersey-json.jar"
JES_JERSEY_SERVER_CLASSPATH="$JES_HOME/jersey/lib/jersey-core.jar:$JES_HOME/jersey/lib/jersey-server.jar:$JES_HOME/jersey/lib/jersey-json.jar"
JES_JERSEY_SERVLET_CLASSPATH="$JES_JERSEY_SERVER_CLASSPATH:$JES_HOME/jersey/lib/jersey-servlet.jar"
JES_JERSEY_CLASSPATH="$JES_HOME/jersey/lib/asm.jar:$JES_JERSEY_SERVER_CLASSPATH"

JES_CLASSPATH="$SMARTMETER_CLASSPATH:$JES_JERSEY_CLASSPATH:$JES_GLASSFISH_CLASSPATH:$JES_JERSEY_SERVLET_CLASSPATH:$JES_JAVADB_CLASSPATH"

java -Xmx128m -Djava.library.path=/usr/lib/jni -classpath $JES_CLASSPATH com.oracle.jes.util.webhost.LightweightHost -port 8080 -timeout 604800 /home/pi/smartmeter/SmartMeter.jar nl.biemond.smartmeter.SmartMeterApplication