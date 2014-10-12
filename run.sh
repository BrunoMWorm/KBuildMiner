#!/bin/sh
java -ea -Xmx2G -Xms128m -Xss20m -classpath "target/classes:$(cat .classpath-scala)" "$@"
