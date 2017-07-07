package com.rafaelgarrote.utad.twitterstreaming.model

import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes

case class AnalysisResult(tweetText: String, sentiment: Option[String], entities: List[String]) extends Serializable

object AnalysisResult extends Serializable {
  implicit val writer: Writes[AnalysisResult] = Json.writes[AnalysisResult]
  implicit val reader: Reads[AnalysisResult] = Json.reads[AnalysisResult]
}
