/*
 * Copyright (C) 2015 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rafaelgarrote.utad.twitterstreaming.model

import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes

case class EnrichedTweet(
                          id: Long,
                          author: Author,
                          text: String,
                          date: Long,
                          mentions: List[Long],
                          hashTags: List[String],
                          urls: List[String],
                          isRT: Boolean,
                          favoriteCount: Long,
                          RTCount: Long,
                          sentiment: Option[String],
                          entities: List[String]
                        )

object EnrichedTweet {
  implicit val writer: Writes[EnrichedTweet] = Json.writes[EnrichedTweet]
  implicit val reader: Reads[EnrichedTweet] = Json.reads[EnrichedTweet]
}