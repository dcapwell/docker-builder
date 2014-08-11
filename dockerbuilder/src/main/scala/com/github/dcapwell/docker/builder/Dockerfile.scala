package com.github.dcapwell.docker.builder

import com.github.dcapwell.docker.builder.lang.Trait

import scala.annotation.tailrec

object Dockerfile {

  def generate(traits: List[Trait]): List[String] = {
    @tailrec
    def loop(traits: List[Trait], accum: List[String]): List[String] = traits match {
      case x :: xs =>
        val fromStr = x.from.map(_.toString).toList
        val restStr = x.instructions.map(_.toString)

        loop(xs, accum ++ fromStr ++ restStr)
      case Nil => accum
    }

    if(traits.isEmpty) Nil
    else loop(traits, Nil)
  }
}
