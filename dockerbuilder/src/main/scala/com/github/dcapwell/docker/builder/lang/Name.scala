package com.github.dcapwell.docker.builder.lang

import scalaz.std.option._
import scalaz.syntax.std.option._

case class Name(name: String) extends Tree

object NameExtract {
  def unapply(data: (String, String)): Option[Name] =
    if (data._1.toLowerCase() == "name") Name(data._2).some
    else none
}
