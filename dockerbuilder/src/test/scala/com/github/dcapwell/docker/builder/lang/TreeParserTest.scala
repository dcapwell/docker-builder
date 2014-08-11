package com.github.dcapwell.docker.builder.lang

import org.scalatest.{FreeSpec, Matchers}
import scalaz.syntax.std.option._

import scala.io.Source

class TreeParserTest extends FreeSpec with Matchers {
  "parse simple os meta file" in {
    val content =
      """
        |# base image for centos
        |
        |FROM: centos:centos6
        |NAME: rhel
        |TAG: centos6
      """.stripMargin

    val tree = Tree.parse(Source.fromString(content).getLines())
    println(s"Tree is $tree")

    tree shouldBe List(
      From(Image("centos"), Tag("centos6")),
      Name("rhel"),
      Tag("centos6")
    ).some
  }
}
