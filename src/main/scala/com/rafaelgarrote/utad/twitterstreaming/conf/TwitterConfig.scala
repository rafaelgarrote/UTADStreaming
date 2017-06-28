package com.rafaelgarrote.utad.twitterstreaming.conf

import twitter4j.auth.{Authorization, AuthorizationFactory}
import twitter4j.conf.ConfigurationBuilder

import scala.util.Try

object TwitterConfig {

  lazy val conf = new ConfigurationBuilder()
  conf.setDebugEnabled(AppProperties.getTwitterDebug)
  conf.setOAuthConsumerKey(AppProperties.getTwitterOauthConsumerKey)
  conf.setOAuthConsumerSecret(AppProperties.getTwitterOauthConsumerSecret)
  conf.setOAuthAccessToken(AppProperties.getTwitterOauthAccessToken)
  conf.setOAuthAccessTokenSecret(AppProperties.getTwitterOauthAccessTokenSecret)

  def getAuthorizationFactoryInstance: Option[Authorization] =
    Try(AuthorizationFactory.getInstance(conf.build())).toOption

}
