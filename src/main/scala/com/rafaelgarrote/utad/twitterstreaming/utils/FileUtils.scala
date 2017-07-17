package com.rafaelgarrote.utad.twitterstreaming.utils

import scala.io.Source

object FileUtils {

  def parseEntitiesFile(url: String): List[String] = Source.fromFile(url).getLines.toList

}
