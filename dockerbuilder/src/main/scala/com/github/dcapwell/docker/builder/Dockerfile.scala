package com.github.dcapwell.docker.builder

import com.github.dcapwell.docker.builder.lang.Trait
import scalaz.syntax.show._

object Dockerfile {
  import com.github.dcapwell.docker.builder.lang.Instruction._

  def generate(traits: List[Trait]): String = {
    def loop(current: Trait, rest: List[Trait], accum: StringBuilder): StringBuilder = {
      current.from.foreach(f => accum.append(f.shows))

//      current.instructions.foreach(i => accum.append(i.shows))

      if(rest.isEmpty) accum
      else loop(rest.head, rest.tail, accum)
    }
    loop(traits.head, traits.tail, new StringBuilder).toString
  }
}
