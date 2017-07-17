package com.rafaelgarrote.utad.twitterstreaming.model

case class Author(
                   id: Long,
                   name: String,
                   screenName: Option[String],
                   location: Option[String]
                 )
