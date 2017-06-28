package com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis

import com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.dandelion.model.EntitiesAnalysisResult

import scala.util.Try

trait SentimentAnalysisProvider {

  def extractEntities(text: String): Try[EntitiesAnalysisResult]

}
