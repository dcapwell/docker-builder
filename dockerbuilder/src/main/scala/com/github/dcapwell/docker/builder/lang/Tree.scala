package com.github.dcapwell.docker.builder.lang

import scalaz._, Scalaz._

trait Tree extends Any

object Tree {
  def lex(content: Iterator[String]): Option[List[Tree]] =
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
    case TagExtract(t) => t.some
    case NameExtract(n) => n.some
    case SelfExtract(s) => s.some
    case RunExtract(r) => r.some
    case _ => none
  }
}
