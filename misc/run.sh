#!/bin/sh

java \
  -Xmx8m \
  -Xms8m \
  -XX:+PrintGCDetails \
  -XX:+PrintGCTimeStamps \
  -XX:TieredStopAtLevel=2 \
  -XX:+UseSerialGC \
  -Dfelix.log.level=4 \
  -Dfelix.startlevel.bundle=5 \
  -Dlogback.configurationFile=misc/logback.xml \
  -Dorg.osgi.framework.startlevel.beginning=5 \
  -Dorg.osgi.framework.storage.clean=onFirstInit \
  -Dorg.osgi.framework.storage=/tmp/thoth \
  -Dorg.osgi.framework.system.packages.extra="javax.annotation;version=3.0, slf4j.api" \
  -Dorg.osgi.framework.bootdelegation="javax.annotation;version=3.0, slf4j.api" \
  -jar ${HOME}/var/felix/felix-framework-5.4.0/bin/felix.jar \
  -b /tmp/thoth-run

