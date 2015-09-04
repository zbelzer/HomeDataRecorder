#!/usr/bin/env bash
mvn install:install-file -Dfile=libs/xbjlib-1.1.0.jar -DgroupId=com.digi.xbee -DartifactId=xbjlib -Dversion=1.1.0 -Dpackaging=pom -DgeneratePom=true
mvn install:install-file -Dfile=libs/rxtx-2.2.jar -DgroupId=gnu.io.rxtx -DartifactId=rxtx -Dversion=2.2 -Dpackaging=jar -DgeneratePom=true
