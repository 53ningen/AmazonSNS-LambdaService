name := "snsservice"

version := "1.0.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-lambda-java-core" % "1.1.0",
  "com.amazonaws" % "aws-lambda-java-events" % "1.1.0",
  "com.amazonaws" % "aws-java-sdk-sns" % "1.10.76",
  "org.json4s" %% "json4s-native" % "3.3.0",
  "com.typesafe" % "config" % "1.3.0"
)

