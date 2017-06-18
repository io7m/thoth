#!/bin/sh

exec java \
  -Xmx16m \
  -Xms16m \
  -XX:MetaspaceSize=32m \
  -XX:MaxMetaspaceSize=32m \
  -XX:-UseCompressedClassPointers \
  -XX:+PrintGCDetails \
  -XX:+PrintGCTimeStamps \
  -XX:TieredStopAtLevel=2 \
  -XX:+UseSerialGC \
  -Dgosh.args="--nointeractive" \
  -jar lib/org.apache.felix.main-5.6.1.jar
