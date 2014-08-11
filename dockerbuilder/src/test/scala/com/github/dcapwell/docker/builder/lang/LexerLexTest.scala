package com.github.dcapwell.docker.builder.lang

import scalaz.Scalaz._

import scala.io.Source

class LexerLexTest extends Base {

  private[this] implicit def StringToIterator(content: String): Iterator[String] =
    Source.fromString(content).getLines()

  "lex simple os meta file" in {
    val content = source("traits/centos.docker")

    val tree = Lexer.lex(content)
    println(s"Tree is $tree")

    tree shouldBe List(
      From(Image(Name("centos"), Tag("centos6"))),
      Named(Image(Name("rhel"), Tag("centos6")))
    ).some
  }

  "lex java mixin" in {
    val content = source("traits/openjdk-7.docker")
    val tree = Lexer.lex(content)
    println(s"Tree is $tree")

    tree shouldBe List(
      Self(List(Name("rhel"))),
      Named(Image(Name("java"), Tag("openjdk-1.7"))),
      Run("yum install -y java-1.7.0-openjdk-devel")
    ).some
  }

  "lex image with self bounds" in {
    val content = source("traits/hadoop-datanode-24.docker")
    val tree = Lexer.lex(content)
    println(s"Tree is $tree")

    tree shouldBe List(
      Self(List(
        Name("rhel"),
        Name("java")
      )),
      Named(Image(Name("datanode"), Tag("datanode-2.4"))),
      Run("mkdir -p /opt/hadoop"),
      Run("cd /opt/hadoop && wget 'http://mirrors.koehn.com/apache/hadoop/common/hadoop-2.4.1/hadoop-2.4.1.tar.gz' && tar zxvf hadoop-2.4.1.tar.gz && rm hadoop-2.4.1.tar.gz")
    ).some
  }

//  "centos with java is valid" in {
//    val centosContent =
//      """
//        |# base image for centos
//        |
//        |FROM: centos:centos6
//        |
//        |NAME: rhel
//        |TAG: centos6
//      """.stripMargin
//
//    val centos = Tree.lex(centosContent).get
//
//    val javaContent =
//      """
//        |# mixin to add java 7 support
//        |
//        |SELF: rhel
//        |
//        |NAME: java
//        |TAG: openjdk-1.7
//        |
//        |RUN: yum install -y java-1.7.0-openjdk-devel
//      """.stripMargin
//
//    val java = Tree.lex(javaContent).get
//
//    val typeChecked = Tree.typecheck(List(centos, java))
//
//    typeChecked shouldBe List(centos, java).success[Error]
//  }
//
//  "java with centos is not valid" in {
//    val centosContent =
//      """
//        |# base image for centos
//        |
//        |FROM: centos:centos6
//        |
//        |NAME: rhel
//        |TAG: centos6
//      """.stripMargin
//
//    val centos = Tree.lex(centosContent).get
//
//    val javaContent =
//      """
//        |# mixin to add java 7 support
//        |
//        |SELF: rhel
//        |
//        |NAME: java
//        |TAG: openjdk-1.7
//        |
//        |RUN: yum install -y java-1.7.0-openjdk-devel
//      """.stripMargin
//
//    val java = Tree.lex(javaContent).get
//
//    val typeChecked = Tree.typecheck(List(java, centos))
//
//    val expectedError = Error("Unable to validate requirement for 'java': 'rhel' not found on left hand side")
//    typeChecked shouldBe expectedError.fail[List[Trait]]
//  }
}
