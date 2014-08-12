package com.github.dcapwell.docker.builder.lang

import scalaz.{Validation, NonEmptyList, ValidationNel}
import scalaz.Scalaz._

object TypeChecker {
  def typecheck(traits: List[Trait]): ValidationNel[Error, List[Trait]] = traits.reverse match {
    case x :: xs =>
      typecheck(x, xs).flatMap(_ => traits.successNel[Error])
    case Nil =>
      traits.successNel[Error]
  }

  private[this] def typecheck(t: Trait, rest: List[Trait]): ValidationNel[Error, List[Trait]] = {
    (onlyRootContainsFrom(t, rest) |@| selfReqsInRest(t, rest)) {(first, second) => first}
  }

  private[this] def onlyRootContainsFrom(t: Trait, rest: List[Trait]): ValidationNel[Error, List[Trait]] =
  t.from match {
    case Some(from) if rest.nonEmpty =>
      Error(s"Only root trait can define the FROM instruction; ${t.named} has ${from}").failureNel
    case _ => (t :: rest).successNel
  }

  private[this] def selfReqsInRest(t: Trait, rest: List[Trait]): ValidationNel[Error, List[Trait]] = {
    def containsName(list: List[Trait])(name: Name): Boolean = list match {
      case x :: xs => x.named match {
        case Some(named) =>
          if(named.image.name == name) true
          else containsName(xs)(name)
        case None => containsName(xs)(name)
      }
      case Nil => false
    }

    t.self match {
      case Some(self) =>
        if(rest.isEmpty) Error(s"Self requirements not satisfied; ${t.named} requires ${self.self}").failureNel
        else {
          if(self.self.forall(containsName(rest) _)) (t :: rest).successNel
          else Error("Self requirements not fully satisfied").failureNel
        }
      case None => (t :: rest).successNel
    }
  }
}
