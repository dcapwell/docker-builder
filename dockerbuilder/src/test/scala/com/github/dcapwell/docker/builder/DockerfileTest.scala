package com.github.dcapwell.docker.builder

import com.github.dcapwell.docker.builder.lang.{Lexer, Trait}

class DockerfileTest extends Base {
  val centos = Trait.unapply(Lexer.lex(source("traits/centos.docker")).get).get
  val java = Trait.unapply(Lexer.lex(source("traits/openjdk-7.docker")).get).get

  "centos with java" in {
    val content = Dockerfile.generate(List(centos, java))
    println(s"Content\n${content.mkString("\n")}")

    content shouldBe List(
      "FROM centos:centos6",
      "RUN yum install -y java-1.7.0-openjdk-devel"
    )
  }

  "nil" in {
    Dockerfile.generate(Nil) shouldBe Nil
  }

  "centos" in {
    Dockerfile.generate(List(centos)) shouldBe List("FROM centos:centos6")
  }
}
