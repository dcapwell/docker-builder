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
        |
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

  "parse java mixin" in {
    val content =
      """
        |# mixin to add java 7 support
        |
        |SELF: rhel
        |
        |NAME: java
        |TAG: openjdk-1.7
        |
        |RUN: yum install -y java-1.7.0-openjdk-devel
      """.stripMargin

    val tree = Tree.parse(Source.fromString(content).getLines())
    println(s"Tree is $tree")

    tree shouldBe List(
      Self(List(Name("rhel"))),
      Name("java"),
      Tag("openjdk-1.7"),
      Run("yum install -y java-1.7.0-openjdk-devel")
    ).some
  }

  "parse image with self bounds" in {
    val content =
      """
        |# mixin to add datanode
        |
        |SELF: rhel with java
        |
        |NAME: datanode
        |TAG: datanode-2.4
        |
        |RUN: mkdir -p /opt/hadoop
        |RUN: cd /opt/hadoop && wget 'http://mirrors.koehn.com/apache/hadoop/common/hadoop-2.4.1/hadoop-2.4.1.tar.gz' && tar zxvf hadoop-2.4.1.tar.gz && rm hadoop-2.4.1.tar.gz
      """.stripMargin

    val tree = Tree.parse(Source.fromString(content).getLines())
    println(s"Tree is $tree")

    tree shouldBe List(
      Self(List(
        Name("rhel"),
        Name("java")
      )),
      Name("datanode"),
      Tag("datanode-2.4"),
      Run("mkdir -p /opt/hadoop"),
      Run("cd /opt/hadoop && wget 'http://mirrors.koehn.com/apache/hadoop/common/hadoop-2.4.1/hadoop-2.4.1.tar.gz' && tar zxvf hadoop-2.4.1.tar.gz && rm hadoop-2.4.1.tar.gz")
    ).some
  }
}
