package com.github.dcapwell.docker.builder.lang

import com.github.dcapwell.docker.builder.Base

import scalaz.Scalaz._

class TraitTest extends Base {

  "centos is valid trait" in {
    val content = source("traits/centos.docker")

    val tree = Lexer.lex(content).get
    println(s"Tree is $tree")

    val t = Trait.unapply(tree).get
    println(s"Trait is $t")

    t.from shouldBe From(Image(Name("centos"), Tag("centos6"))).some
    t.named shouldBe Named(Image(Name("rhel"), Tag("centos6"))).some
    t.instructions shouldBe Nil
  }

  "self trait" in {
    val content = source("traits/hadoop-datanode-24.docker")

    val tree = Lexer.lex(content).get
    println(s"Tree is $tree")

    val t = Trait.unapply(tree).get
    println(s"Trait is $t")

    t.self shouldBe Self(List(Name("rhel"), Name("java"))).some
    t.named shouldBe Named(Image(Name("datanode"), Tag("datanode-2.4"))).some
    t.instructions shouldBe List(
      Run("mkdir -p /opt/hadoop"),
      Run("cd /opt/hadoop && wget 'http://mirrors.koehn.com/apache/hadoop/common/hadoop-2.4.1/hadoop-2.4.1.tar.gz' && tar zxvf hadoop-2.4.1.tar.gz && rm hadoop-2.4.1.tar.gz")
    )
  }
}
