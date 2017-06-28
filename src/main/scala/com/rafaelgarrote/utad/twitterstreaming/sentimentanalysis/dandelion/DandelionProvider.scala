package com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.dandelion

import java.net.URLEncoder

import com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.SentimentAnalysisProvider
import com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.dandelion.model.EntitiesAnalysisResult
import com.rafaelgarrote.utad.twitterstreaming.utils.HTTPUtils
import play.api.libs.json.Json

import scala.util.Try

object DandelionProvider extends SentimentAnalysisProvider {

  val URL_BASE = "https://api.dandelion.eu/datatxt/nex/v1/"

  def extractEntitiesUrl(text: String): String = s"$URL_BASE?min_confidence=0.6&social.hashtag=true&" +
    s"socialmention=true&text=$text&include=types%2Ccategories%2Clod%2Calternate_labels1&" +
    s"token=686740f7c1f645d8b746139d822e078f"

  def extractEntities(text: String): Try[EntitiesAnalysisResult] = {
    val textEncoded = URLEncoder.encode(text)
    HTTPUtils.doGet(extractEntitiesUrl(textEncoded)).map(Json.parse(_).as[EntitiesAnalysisResult])
  }
}
