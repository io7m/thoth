#!/bin/sh

exec java \
  -Xmx8m \
  -Xms8m \
  -XX:MetaspaceSize=20m \
  -XX:MaxMetaspaceSize=20m \
  -XX:-UseCompressedClassPointers \
  -XX:+PrintGCDetails \
  -XX:+PrintGCTimeStamps \
  -XX:TieredStopAtLevel=2 \
  -XX:+UseSerialGC \
  -jar lib/org.apache.felix.main-5.6.1.jar \
