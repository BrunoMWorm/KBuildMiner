name := "iml-parser"

organization := "gsd"

version := "dev_1.1"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
"net.sf.squirrel-sql.thirdparty-non-maven" % "java-cup" % "0.11a"
)

scalaSource in Compile := file("src/")

checksums in update := Nil
