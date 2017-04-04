name := """oververse"""

version := "0.2.0.SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies += jdbc
libraryDependencies += cache
libraryDependencies += ws
libraryDependencies += filters
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "2.7.17" % Test
libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.12.1"
libraryDependencies += "org.abstractj.kalium" % "kalium" % "0.5.0"

coverageExcludedPackages := "filters.*;router.*;controllers.javascript.*;views.*;<empty>;Reverse.*;connectors.*;config.*"
coverageMinimum := 100
coverageFailOnMinimum := true

fork := true
javaOptions += "-Xmx4G"

