name := "cdltools"

organization := "gsd"

version := "0.2"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
"junit" % "junit" % "4.7" % "test",
"com.thoughtworks.paranamer" % "paranamer" % "1.3",
"gsd" % "iml-parser_2.9.1" % "dev_1.1",
"com.googlecode" % "kiama_2.9.0-1" % "1.1.0"
)

checksums in update := Nil
