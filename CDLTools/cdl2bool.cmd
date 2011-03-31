@echo off
mvn scala:run -q -DmainClass=gsd.cdl.Iml2BoolMain -DaddArgs="%1|%2|%3|%4|%5|%6|%7"
rem mvn -e exec:java -Dexec.mainClass=gsd.cdl.Iml2BoolMain -Dexec.args="%1|%2|%3|%4|%5|%6|%7"
