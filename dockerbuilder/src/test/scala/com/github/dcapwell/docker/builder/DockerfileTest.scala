package com.github.dcapwell.docker.builder

import com.github.dcapwell.docker.builder.lang.{Lexer, Trait}

class DockerfileTest extends Base {
  "centos with java" in {
    val centos = Trait.unapply(Lexer.lex(source("traits/centos.docker")).get).get
    val java = Trait.unapply(Lexer.lex(source("traits/openjdk-7.docker")).get).get

    val content = Dockerfile.generate(List(centos, java))
    println(s"Content\n$content")
  }
}
