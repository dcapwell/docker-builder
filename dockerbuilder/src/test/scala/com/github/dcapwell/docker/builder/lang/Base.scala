package com.github.dcapwell.docker.builder.lang

import org.scalatest.{Matchers, FreeSpec}

import scala.io.Source

abstract class Base extends FreeSpec with Matchers {
  def source(name: String) =
    Source.fromURL(Thread.currentThread().getContextClassLoader.getResource(name)).getLines()
}
