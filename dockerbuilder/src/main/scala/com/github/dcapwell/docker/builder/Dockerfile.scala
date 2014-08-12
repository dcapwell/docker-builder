package com.github.dcapwell.docker.builder

import com.github.dcapwell.docker.builder.lang._

import scala.annotation.tailrec
import scalaz.syntax.show._
import lang.Instruction.docker._

object Dockerfile {
  def generate(traits: List[Trait]): List[String] = {
    @tailrec
    def loop(traits: List[Trait], accum: List[String]): List[String] = traits match {
      case x :: xs =>
        val fromStr = x.from.map(_.shows).toList
        val restStr = x.instructions.map(_.shows)

        loop(xs, accum ++ fromStr ++ restStr)
      case Nil => accum
    }

    if(traits.isEmpty) Nil
    else loop(traits, Nil)
  }
}
