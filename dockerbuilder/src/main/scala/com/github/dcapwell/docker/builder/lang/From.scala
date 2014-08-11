package com.github.dcapwell.docker.builder.lang

import scalaz.std.option._
import scalaz.syntax.std.option._

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
