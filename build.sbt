name := """oververse"""

version := "0.1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies += jdbc
libraryDependencies += cache
libraryDependencies += ws
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "2.7.17" % Test

coverageExcludedPackages := "filters.*;router.*;controllers.javascript.*;views.*;<empty>;Reverse.*"
coverageMinimum := 100
coverageFailOnMinimum := true

fork := true
javaOptions += "-Xmx4G"

