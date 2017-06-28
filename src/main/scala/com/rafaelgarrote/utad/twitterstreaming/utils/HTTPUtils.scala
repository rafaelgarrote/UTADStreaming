package com.rafaelgarrote.utad.twitterstreaming.utils

import scala.io._
import scala.util.Try

object HTTPUtils {

  def doGet(url: String): Try[String] = Try(Source.fromURL(url)).map(_.mkString)

  def doPost(url: String, body: String) = ???

}
