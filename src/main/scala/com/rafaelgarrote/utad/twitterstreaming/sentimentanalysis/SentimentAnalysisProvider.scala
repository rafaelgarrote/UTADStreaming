package com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis

import scala.util.Try

trait SentimentAnalysisProvider {

  def extractEntities(text: String): Try[String]

}
