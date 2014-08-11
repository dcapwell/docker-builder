package com.github.dcapwell.docker.builder.lang

import scalaz.std.option._
import scalaz.syntax.std.option._

case class Run(command: String) extends Tree

object RunExtract {
  def unapply(data: (String, String)): Option[Run] =
    if(data._1.toLowerCase() == "run") Run(data._2).some
    else none
}
