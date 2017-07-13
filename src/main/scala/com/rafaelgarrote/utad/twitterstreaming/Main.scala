package com.rafaelgarrote.utad.twitterstreaming

import com.rafaelgarrote.utad.twitterstreaming.conf.AppProperties
import com.rafaelgarrote.utad.twitterstreaming.conf.TwitterConfig
import com.rafaelgarrote.utad.twitterstreaming.spark.SparkContextBuilder
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.twitter.TwitterUtils
import org.neo4j.spark.Neo4j
import twitter4j.Status

object Main {

  def main(args: Array[String]): Unit = {
    lazy val conf = AppProperties.config
    val (sparkSession, ssc) = SparkContextBuilder.createSessionStreamingContexts(conf)
    implicit val session = sparkSession
    implicit val sqlContext = session.sqlContext
    implicit val neo: Neo4j = Neo4j(session.sparkContext)
    ssc.checkpoint("/tmp/")

    val rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.ERROR)

    import com.rafaelgarrote.utad.twitterstreaming.spark.TweetAnalysisDsl._

    val stream = TwitterUtils.createStream(ssc, twitterAuth = TwitterConfig.getAuthorizationFactoryInstance)
    val filteredTweets: DStream[Status] = stream.filter(_.getLang == "es").cache()

    val statusAnalizedStream = filteredTweets.analyzeTweet.cache()
    statusAnalizedStream.enrichTweetAndPersist
    statusAnalizedStream.crawlRTsAndPersist

    ssc.start()
    ssc.awaitTermination()

  }
}
