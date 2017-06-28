package com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.dandelion.model

import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes

case class EntitiesAnalysisResult(
                                   time: Int,
                                   annotations: List[Annotation],
                                   lang: String,
                                   langConfidence: Float,
                                   timestamp: String
                                 )

object EntitiesAnalysisResult {

  implicit val writer: Writes[EntitiesAnalysisResult] = Json.writes[EntitiesAnalysisResult]
  implicit val reader: Reads[EntitiesAnalysisResult] = Json.reads[EntitiesAnalysisResult]

}
