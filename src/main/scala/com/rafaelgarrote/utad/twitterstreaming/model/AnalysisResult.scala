package com.rafaelgarrote.utad.twitterstreaming.model

import twitter4j.Status

case class AnalysisResult(
                           status: Status,
                           sentiment: Option[String],
                           entities: List[String]
                         ) extends Serializable

