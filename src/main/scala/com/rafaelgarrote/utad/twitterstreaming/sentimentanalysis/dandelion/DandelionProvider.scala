package com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.dandelion

import java.net.URLEncoder

import com.rafaelgarrote.utad.twitterstreaming.conf.AppProperties
import com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.SentimentAnalysisProvider
import com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.dandelion.model.EntitiesAnalysisResult
import com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.dandelion.model.SentimentAnalysisResult
import com.rafaelgarrote.utad.twitterstreaming.utils.HTTPUtils
import play.api.libs.json.Json

import scala.util.Try

object DandelionProvider extends SentimentAnalysisProvider with Serializable {

  private val URL_BASE = "https://api.dandelion.eu/datatxt"
  private val API_VERSION = "v1"
  private val TOKEN = AppProperties.getDandelionToken

  private def extractEntitiesUrl(text: String): String = s"$URL_BASE/nex/$API_VERSION/?min_confidence=0.6&social" +
    s".hashtag=true&socialmention=true&text=$text&include=types%2Ccategories%2Clod%2Calternate_labels1&" +
    s"token=$TOKEN"

  private def extractSentimentUrl(text: String): String = s"$URL_BASE/sent/$API_VERSION/?text=$text&"+
    s"token=$TOKEN"

  private def textClasificationUrl(text: String): String = s"$URL_BASE/cl/$API_VERSION/?text=$text&"+
    s"model=news_eng&token=$TOKEN"

  def extractEntities(text: String): Try[EntitiesAnalysisResult] = {
    val textEncoded = URLEncoder.encode(text)
    HTTPUtils.doGet(extractEntitiesUrl(textEncoded)).map(Json.parse(_).as[EntitiesAnalysisResult])
  }

  def extractSentiment(text: String): Try[SentimentAnalysisResult] = {
    val textEncoded = URLEncoder.encode(text)
    HTTPUtils.doGet(extractSentimentUrl(textEncoded)).map(Json.parse(_).as[SentimentAnalysisResult])
  }
}
