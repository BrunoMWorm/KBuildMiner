#!/bin/sh
BASEDIR=$(dirname "$0")
java -ea -Xmx2G -Xms128m -Xss50m -classpath "$BASEDIR/target/classes:$(cat $BASEDIR/.classpath-scala)" "$@"
