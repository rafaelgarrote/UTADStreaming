package com.rafaelgarrote.utad.twitterstreaming.conf

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import scala.util.Try

object AppProperties {

  lazy private val config: Config = ConfigFactory.load()

  private val twitterDebugKey = "twitter.debug"
  private val twitterDebugDefaultValue = false
  private val twitterOauthConsumerKey = "twitter.oauth.consumerKey"
  private val twitterOauthConsumerSecretKey = "twitter.oauth.consumerSecret"
  private val twitterOauthAccessTokenKey = "twitter.oauth.accessToken"
  private val twitterOauthAccessTokenSecretKey = "twitter.oauth.accessTokenSecret"

  def getTwitterDebug: Boolean = Try(config.getBoolean(twitterDebugKey)).getOrElse(twitterDebugDefaultValue)
  def getTwitterOauthConsumerKey: String = Try(config.getString(twitterOauthConsumerKey)).getOrElse("")
  def getTwitterOauthConsumerSecret: String = Try(config.getString(twitterOauthConsumerSecretKey)).getOrElse("")
  def getTwitterOauthAccessToken: String = Try(config.getString(twitterOauthAccessTokenKey)).getOrElse("")
  def getTwitterOauthAccessTokenSecret: String = Try(config.getString(twitterOauthAccessTokenSecretKey)).getOrElse("")

}
