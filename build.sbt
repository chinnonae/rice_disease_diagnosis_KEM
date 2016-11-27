name := "rice_disease_diagnosis_kem"

version := "1.0"

lazy val `rice_disease_diagnosis_kem` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

javaOptions in console += "-Djava.library.path=/usr/lib/swi-prolog/lib/amd64/"

fork in console := true

libraryDependencies ++= Seq(
  cache,
  ws,
  specs2 % Test,
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "org.postgresql" % "postgresql" % "9.4.1212",
  "com.pauldijou" %% "jwt-play-json" % "0.9.2"
)


