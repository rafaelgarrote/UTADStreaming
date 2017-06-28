package com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.dandelion.model

import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes

case class Sentiment(`type`: String, score: Float)

object Sentiment {

  implicit val writer: Writes[Sentiment] = Json.writes[Sentiment]
  implicit val reader: Reads[Sentiment] = Json.reads[Sentiment]

}
