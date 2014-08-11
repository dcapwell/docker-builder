package com.github.dcapwell.docker.builder.lang

import scalaz.std.option._
import scalaz.syntax.std.option._

case class Self(self: List[Name]) extends Tree

object SelfExtract {
  def unapply(data: (String, String)): Option[Self] =
    if(data._1.toLowerCase() == "self") {
      Self(data._2.toLowerCase().split("with").map(_.trim).map(Name.apply).toList).some
    }
    else none
}
