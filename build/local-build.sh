#!/bin/sh
echo "[$0]Update jar's /META-INF/spring.handlers" ...
cd ../
mvn package -Dmaven.test.skip=true
cd build/local/
jar uvf ../../target/Ding-Java-1.0-SNAPSHOT-jar-with-dependencies.jar *
echo Success

