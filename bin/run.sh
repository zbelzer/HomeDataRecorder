#!/usr/bin/env bash

nohup java -Dserial.baudRate=9600 \
-Dserial.port=/dev/ttyAMA0 \
-Ddb.name=keggerator \
-Ddb.user=pi \
-Djava.library.path=/usr/lib/jni \
-Dgnu.io.rxtx.SerialPorts=/dev/ttyAMA0 \
-jar target/home-data-recorder-1.0-SNAPSHOT-jar-with-dependencies.jar \
< /dev/null > /var/log/home-data-recorder.log 2>&1 &
