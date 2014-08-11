package com.github.dcapwell.docker.builder

import com.github.dcapwell.docker.builder.lang.Trait

import scala.annotation.tailrec

object Dockerfile {

  def generate(traits: List[Trait]): List[String] = {
    @tailrec
    def loop(current: Trait, rest: List[Trait], accum: List[String]): List[String] = {
      val fromStr = current.from.map(_.toString).toList

      val restStr = current.instructions.map(_.toString)

      if(rest.isEmpty) accum ++ fromStr ++ restStr
      else loop(rest.head, rest.tail, accum ++ fromStr ++ restStr)
    }

    if(traits.isEmpty) Nil
    else loop(traits.head, traits.tail, Nil)
  }
}
