name := """play-rpm-packed"""

version := "1.0-SNAPSHOT"

version in Rpm := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala, RpmPlugin, RpmDeployPlugin)

rpmVendor := "wshino"

rpmGroup := Some("wshino")

rpmLicense := Some("BSD")

rpmBrpJavaRepackJars := true

credentials += Credentials("Sonatype Nexus Repository Manager", "202.6.245.41", "admin", "admin123")

publishTo := {
  val nexus = "http://202.6.245.41:8081/nexus/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/i3_middleware")
  else
    Some("releases"  at nexus + "content/repositories/i3_middleware")
}


scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
