@echo off
mvn scala:run -q -DmainClass=gsd.cdl.CDLModelVisualizationMain -DaddArgs="%1|%2|%3|%4|%5|%6|%7"
