package com.github.dcapwell.docker.builder.lang

import shapeless.{Coproduct, CNil, :+:}
import scalaz.Scalaz._

trait Trait {
  type Parent = From :+: Self :+: CNil

  def parent: Parent

  def from: Option[From] =
    parent.select[From]

  def self: Option[Self] =
    parent.select[Self]

  def named: Option[Named]

  def instructions: List[Instruction]
}

case class SimpleTrait(parent: Trait#Parent,
                       named: Option[Named],
                       instructions: List[Instruction]) extends Trait {
  override def toString: String = named match {
    case Some(name) =>
      s"Trait(${parent}, ${name})"
    case None =>
      s"Trait(${parent})"
  }
}

object Trait {
  def unapply(instructions: List[Instruction]): Option[Trait] = instructions match {
    case x :: xs =>
      extractParent(x) map { p =>
        val (named, rest) = extractName(xs)
        SimpleTrait(p, named, rest)
      }
  }

  private[this] def extractParent(tree: Instruction): Option[Trait#Parent] = tree match {
    case f: From => Coproduct[Trait#Parent](f).some
    case s: Self => Coproduct[Trait#Parent](s).some
    case _ => none
  }

  private[this] def extractName(trees: List[Instruction]): (Option[Named], List[Instruction]) = trees match {
    case x :: xs if x.isInstanceOf[Named] => (x.asInstanceOf[Named].some, xs)
    case _ => (none, trees)
  }
}
