package docker
package builder

import sbt.Keys._
import sbt._

object Build extends sbt.Build {

  def common = Project.defaultSettings ++ Seq[Setting[_]](
    organization := "com.github.dcapwell",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.11.2",
    logBuffered := false,
    scalacOptions ++= Seq("-language:_", "-deprecation"),
    javacOptions ++= Seq("-nowarn", "-XDignore.symbol.file"),
    licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    scalacOptions ++= Seq(Opts.compile.unchecked, Opts.compile.deprecation, Opts.compile.explaintypes),
    publishArtifact in Test := false
  )

  val scalazVersion = "7.1.0"
  val shapelessVersion = "2.0.0"
  val scalatestVersion = "2.2.1"

  lazy val root = project in file(".") dependsOn(dockerBuilder) aggregate(dockerBuilder) settings (common: _*) settings (
    name :=  "docker-builder-root",
    description :=  "Root for the docker builder project",
    initialCommands := "import com.github.dcapwell.docker.builder._, lang._",
    publish :=  (),
    publishLocal :=  (),
    publishArtifact :=  false
  )

  lazy val dockerBuilder = project in file("dockerbuilder") settings (common: _*) settings(
    name := "docker-builder",
    description := "Rethought way to build docker images",
    libraryDependencies ++= Seq(
      "org.scalaz" %% "scalaz-core" % scalazVersion,
      "com.chuusai" %% "shapeless" % shapelessVersion,

      "org.scalatest" %% "scalatest" % scalatestVersion % "test"
    )
  )
}
