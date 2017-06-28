package com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.dandelion.model

import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes

case class Annotation(
                       start: Int,
                       end: Int,
                       spot: String,
                       confidence: Float,
                       id: Int,
                       title: String,
                       uri: String,
                       label: String,
                       categories: List[String],
                       types: List[String],
                       lod: Map[String, String]
                     )

object Annotation {

  implicit val writer: Writes[Annotation] = Json.writes[Annotation]
  implicit val reader: Reads[Annotation] = Json.reads[Annotation]

}
