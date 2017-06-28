package com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis

import java.net.URLEncoder

import com.rafaelgarrote.utad.twitterstreaming.utils.HTTPUtils

import scala.util.Try

object DandelionProvider extends SentimentAnalysisProvider {

  val URL_BASE = "https://api.dandelion.eu/datatxt/nex/v1/"

  def extractEntitiesUrl(text: String): String = s"$URL_BASE?min_confidence=0.6&social=False&text=$text" +
    s"&include=image%2Cabstract%2Ctypes%2Ccategories%2Clod&country=-1&token=686740f7c1f645d8b746139d822e078f"

  def extractEntities(text: String): Try[String] = {
    val textEncoded = URLEncoder.encode(text)
    HTTPUtils.doGet(extractEntitiesUrl(textEncoded))
  }
}
