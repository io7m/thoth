#!/bin/sh -ex

mvn \
  --offline \
  -Dmaven.source.skip=true \
  -Dmaven.javadoc.skip=true \
  -Dcheckstyle.skip=true \
  -DoutputDirectory=/tmp/thoth-run/ \
  -DincludeScope=runtime \
  -Dkstructural.skip=true \
  clean package dependency:copy-dependencies \
  "$@"

cp -v */target/*.jar /tmp/thoth-run
