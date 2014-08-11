package com.github.dcapwell.docker.builder.lang

import scalaz.Scalaz._
import scalaz.Show

trait Instruction extends Any

trait InstructionInstances {
  implicit val imageShow: Show[Image] = Show.shows(i => s"${i.name.name}:${i.tag.tag}")
  implicit val fromShow: Show[From] = Show.show(f => "FROM " + f.image.show)
}

object Instruction extends InstructionInstances

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

case class From(image: Image) extends Instruction

object FromExtract {
  def unapply(data: (String, String)): Option[From] =
    optAm(data, "from")(ImageExtract.unapply).map(From.apply)
}

case class Named(image: Image) extends Instruction

object NamedExtract {
  def unapply(data: (String, String)): Option[Named] =
    optAm(data, "named")(ImageExtract.unapply).map(Named.apply)
}

case class Run(command: String) extends Instruction

object RunExtract {
  def unapply(data: (String, String)): Option[Run] =
    am(data, "run")(Run.apply)
}

case class Self(self: List[Name]) extends Instruction

object SelfExtract {
  def unapply(data: (String, String)): Option[Self] =
  am(data, "self")(c => Self(c.toLowerCase.split("with").map(_.trim).map(Name.apply).toList))
}

object TagExtract {
  def unapply(data: (String, String)): Option[Tag] =
    am(data, "tag")(Tag.apply)
}
