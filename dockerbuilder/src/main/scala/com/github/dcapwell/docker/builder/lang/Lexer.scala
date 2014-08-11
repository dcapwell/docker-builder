package com.github.dcapwell.docker.builder.lang

import scala.io.Source
import scalaz.Scalaz._

object Lexer {
  def lex(content: String): Option[List[Instruction]] =
    lex(Source.fromString(content).getLines())

  def lex(content: Iterator[String]): Option[List[Instruction]] =
    content.
      filterNot(comment).
      filterNot(empty).
      map(toTree).
      toList.
      sequence[Option, Instruction]

  private[this] def empty(line: String): Boolean = line.trim.isEmpty
  private[this] def comment(line: String): Boolean = line.trim.startsWith("#")

  private[this] def toTree(line: String): Option[Instruction] = {
    val index = line.indexOf(':')
    if (index > 0) {
      val name = line.substring(0, index).trim
      val content = line.substring(index + 1).trim
      toTree(name, content)
    } else None
  }

  private[this] def toTree(name: String, content: String): Option[Instruction] = (name, content) match {
    case FromExtract(f) => f.some
    case SelfExtract(s) => s.some
    case NamedExtract(n) => n.some
    case RunExtract(r) => r.some
    case _ => none
  }
}

