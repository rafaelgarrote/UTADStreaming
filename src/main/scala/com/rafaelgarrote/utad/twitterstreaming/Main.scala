package com.rafaelgarrote.utad.twitterstreaming

import com.rafaelgarrote.utad.twitterstreaming.conf.TwitterConfig
import com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.dandelion.DandelionProvider
import com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.standfordnlp.StandfordNlpProvider
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.twitter.TwitterUtils

object Main {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName("Tweet Example").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf, Seconds(10))
    ssc.checkpoint("/tmp/")

    val rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.ERROR)

    val stream = TwitterUtils.createStream(ssc, twitterAuth = TwitterConfig.getAuthorizationFactoryInstance)
    val filteredTweets = stream.filter(_.getLang == "en").cache()
    filteredTweets.map(status =>
      (status.getText,
        DandelionProvider.extractEntities(status.getText).toOption,
        DandelionProvider.extractSentiment(status.getText).toOption)
    ).print()

    filteredTweets.map(status =>
      (status.getText,
        StandfordNlpProvider.extractEntities(status.getText).toOption,
          StandfordNlpProvider.extractSentiment(status.getText).toOption)
    ).print()
    ssc.start()
    ssc.awaitTermination()

  }
}
