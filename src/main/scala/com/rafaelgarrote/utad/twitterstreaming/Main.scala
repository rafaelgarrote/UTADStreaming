package com.rafaelgarrote.utad.twitterstreaming

import com.rafaelgarrote.utad.twitterstreaming.conf.TwitterConfig
import com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.dandelion.DandelionProvider
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object Main {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName("Tweet Example").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf, Seconds(10))
    ssc.checkpoint("/tmp/")

    val rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.ERROR)

    val stream = TwitterUtils.createStream(ssc, twitterAuth = TwitterConfig.getAuthorizationFactoryInstance)
    stream.filter(_.getLang == "es")
      .map(status => (status.getText,DandelionProvider.extractEntities(status.getText).toOption)).print()
    stream.filter(_.getLang == "en")
      .map(status => (status.getText,DandelionProvider.extractSentiment(status.getText).toOption)).print()

    ssc.start()
    ssc.awaitTermination()

  }
}
