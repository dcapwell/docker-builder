package com.github.dcapwell.docker.builder.lang

import com.github.dcapwell.docker.builder.Base

import scalaz.Scalaz._

class TypeCheckerTest extends Base {

  private[this] val centos = Trait.unapply(Lexer.lex(source("traits/centos.docker")).get).get
  private[this] val java = Trait.unapply(Lexer.lex(source("traits/openjdk-7.docker")).get).get
  private[this] val hadoop = Trait.unapply(Lexer.lex(source("traits/hadoop-datanode-24.docker")).get).get

  Seq(
    List(),
    List(centos),
    List(centos, java),
    List(centos, java, hadoop)
  ).foreach { input =>
    s"${input.mkString(" with ")} should typecheck" in {
      val rsp = TypeChecker.typecheck(input)
      rsp shouldBe input.successNel[Error]
    }
  }

  Seq(
    (List(java), Error("Self requirements not satisfied; Some(Named(java:openjdk-1.7)) requires List(Name(rhel))")),
    (List(hadoop), Error("Self requirements not satisfied; Some(Named(datanode:datanode-2.4)) requires List(Name(rhel), Name(java))")),
    (List(hadoop, java), Error("Self requirements not fully satisfied")), //TODO improve
    (List(hadoop, java, centos), Error("Only root trait can define the FROM instruction; Some(Named(rhel:centos6)) has From(centos:centos6)"))
  ).foreach {
    case (input, error) =>
      s"${input.mkString(" with ")} should be rejected typecheck" in {
        val rsp = TypeChecker.typecheck(input)
        rsp shouldNot be(input.successNel[Error])
        rsp shouldBe error.failNel[List[Trait]]
      }
  }
}
