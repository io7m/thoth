#!/bin/sh

exec java \
  -Xmx16m \
  -Xms16m \
  -XX:MetaspaceSize=32m \
  -XX:MaxMetaspaceSize=32m \
  -XX:-UseCompressedClassPointers \
  -XX:+PrintGCDetails \
  -XX:TieredStopAtLevel=2 \
  -XX:+UseSerialGC \
  -Dgosh.args="--nointeractive" \
  -jar lib/org.apache.felix.main-6.0.1.jar
