package com.github.dcapwell.docker.builder.lang

import scalaz.Scalaz._
import scalaz.Validation

case class Error(message: String)

trait Tree extends Any

object Tree {
  def lex(content: Iterator[String]): Option[Trait] =
    content.
      filterNot(comment).
      filterNot(empty).
      map(lex).
      toList.
      sequence[Option, Tree]

  private[this] def empty(line: String): Boolean = line.trim.isEmpty
  private[this] def comment(line: String): Boolean = line.trim.startsWith("#")

  private[this] def lex(line: String): Option[Tree] = {
    val index = line.indexOf(':')
    if (index > 0) {
      val name = line.substring(0, index).trim
      val content = line.substring(index + 1).trim
      lex(name, content)
    } else None
  }

  private[this] def lex(name: String, content: String): Option[Tree] = (name, content) match {
    case FromExtract(f) => f.some
    case SelfExtract(s) => s.some
    case NameExtract(n) => n.some
    case TagExtract(t) => t.some
    case RunExtract(r) => r.some
    case _ => none
  }

  def typecheck(traits: List[Trait]): Validation[Error, List[Trait]] =
    traits.success
}

case class Image(image: String)

case class From(image: Image, tag: Tag) extends Tree

object FromExtract {
  def unapply(data: (String, String)): Option[From] =
    if (data._1.toLowerCase == "from") {
      val split = data._2.split(':')
      if (split.length == 2) {
        From(Image(split(0)), Tag(split(1))).some
      } else none
    } else none
}

case class Name(name: String) extends Tree

object NameExtract {
  def unapply(data: (String, String)): Option[Name] =
    if (data._1.toLowerCase() == "name") Name(data._2).some
    else none
}

case class Run(command: String) extends Tree

object RunExtract {
  def unapply(data: (String, String)): Option[Run] =
    if(data._1.toLowerCase() == "run") Run(data._2).some
    else none
}

case class Self(self: List[Name]) extends Tree

object SelfExtract {
  def unapply(data: (String, String)): Option[Self] =
    if(data._1.toLowerCase() == "self") {
      Self(data._2.toLowerCase().split("with").map(_.trim).map(Name.apply).toList).some
    }
    else none
}

case class Tag(tag: String) extends Tree

object TagExtract {
  def unapply(data: (String, String)): Option[Tag] =
    if (data._1.toLowerCase() == "tag") Tag(data._2).some
    else none
}

