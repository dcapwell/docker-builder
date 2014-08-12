package com.github.dcapwell.docker.builder.lang

import scalaz.Scalaz._
import scalaz.Show

trait Instruction extends Any

trait InstructionDockerShow {
  implicit val imageShow: Show[Image] = Show.shows { image => s"${image.name.name}:${image.tag.tag}"}
  implicit val fromShow: Show[From] = Show.shows { from => s"FROM ${from.image.shows}"}
  implicit val runShow: Show[Run] = Show.shows { run => s"RUN ${run.command}"}
  implicit val instructionShow: Show[Instruction] = Show.show {
    case f: From => f.show
    case r: Run => r.show
  }
}

object Instruction {
  object docker extends InstructionDockerShow
}


case class Name(name: String)
case class Tag(tag: String)
case class Image(name: Name, tag: Tag)

object ImageExtract {
  def unapply(content: String): Option[Image] = {
    val split = content.split(':')
    if (split.length == 2) {
      Image(Name(split(0)), Tag(split(1))).some
    } else none
  }
}

case class From(image: Image) extends Instruction {
  import Instruction.docker._
  override def toString: String = s"From(${image.shows})"
}

object FromExtract {
  def unapply(data: (String, String)): Option[From] =
    optAm(data, "from")(ImageExtract.unapply).map(From.apply)
}

case class Named(image: Image) extends Instruction {
  import Instruction.docker._
  override def toString: String = s"Named(${image.shows})"
}

object NamedExtract {
  def unapply(data: (String, String)): Option[Named] =
    optAm(data, "named")(ImageExtract.unapply).map(Named.apply)
}

case class Run(command: String) extends Instruction

object RunExtract {
  def unapply(data: (String, String)): Option[Run] =
    am(data, "run")(Run.apply)
}

case class Self(self: List[Name]) extends Instruction {
  override def toString: String = s"Self(${self.map(_.name).mkString(" with ")})"
}

object SelfExtract {
  def unapply(data: (String, String)): Option[Self] =
  am(data, "self")(c => Self(c.toLowerCase.split("with").map(_.trim).map(Name.apply).toList))
}
