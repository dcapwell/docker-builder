package com.github.dcapwell.docker.builder

import scalaz.Scalaz._

package object lang {
  private[lang] def am[A](data: (String, String), name: String)(fn: String => A): Option[A] =
    if(data._1.toLowerCase() == name) fn(data._2).some
    else none

  private[lang] def optAm[A](data: (String, String), name: String)(fn: String => Option[A]): Option[A] =
    if(data._1.toLowerCase() == name) fn(data._2)
    else none
}
