package com.rafaelgarrote.utad.twitterstreaming.model

import twitter4j.Status

case class AnalysisResult(
                           status: Status,
                           sentiment: Option[String] = None,
                           entities: List[String] = List.empty[String],
                           mentions: List[String] = List.empty[String]
                         )

