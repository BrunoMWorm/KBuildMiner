#!/bin/sh
java -ea -Xmx2G -Xms128m -Xss10m -classpath "target/classes:$(cat .classpath-scala)" "$@"
