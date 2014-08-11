package com.github.dcapwell.docker.builder.lang

import scalaz.std.option._
import scalaz.syntax.std.option._

case class Tag(tag: String) extends Tree

object TagExtract {
  def unapply(data: (String, String)): Option[Tag] =
    if (data._1.toLowerCase() == "tag") Tag(data._2).some
    else none
}
