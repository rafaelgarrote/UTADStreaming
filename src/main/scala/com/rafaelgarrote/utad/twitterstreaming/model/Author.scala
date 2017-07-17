package com.rafaelgarrote.utad.twitterstreaming.model

import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes

case class Author(
                   id: Long,
                   name: String,
                   screenName: Option[String],
                   location: Option[String]
                 )

object Author {
  implicit val writer: Writes[Author] = Json.writes[Author]
  implicit val reader: Reads[Author] = Json.reads[Author]
}