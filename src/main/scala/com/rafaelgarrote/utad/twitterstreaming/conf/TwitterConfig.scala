package com.rafaelgarrote.utad.twitterstreaming.conf

import twitter4j.auth.{Authorization, AuthorizationFactory}
import twitter4j.conf.ConfigurationBuilder

import scala.util.Try

object TwitterConfig {

  lazy val conf = new ConfigurationBuilder()
  conf.setDebugEnabled(true)
  conf.setOAuthConsumerKey("")
  conf.setOAuthConsumerSecret("")
  conf.setOAuthAccessToken("")
  conf.setOAuthAccessTokenSecret("")

  def getAuthorizationFactoryInstance: Option[Authorization] =
    Try(AuthorizationFactory.getInstance(conf.build())).toOption

}
