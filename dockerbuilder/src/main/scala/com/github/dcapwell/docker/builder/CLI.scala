package com.github.dcapwell.docker.builder

import com.github.dcapwell.docker.builder.lang.{Lexer, Trait}

import scala.io.Source
import scalaz.Scalaz._

object CLI extends App {
  if(args.isEmpty) error("At least one file required")

  val result = args.
    map(a => Source.fromFile(a).getLines).
    map(Lexer.lex).
    map(is => is.flatMap(Trait.unapply)).
    toList.
    sequence[Option, Trait]

  result.map(Dockerfile.generate) match {
    case Some(c) => println(c.mkString("\n"))
    case None => error(s"Unable to parse docker files given: ${args}")
  }

  private[this] def error(msg: String) = {
    Console.err.println(msg)
    System.exit(1)
  }
}
