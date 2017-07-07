package com.rafaelgarrote.utad.twitterstreaming

import com.rafaelgarrote.utad.twitterstreaming.conf.AppProperties
import com.rafaelgarrote.utad.twitterstreaming.conf.TwitterConfig
import com.rafaelgarrote.utad.twitterstreaming.model.AnalysisResult
import com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.dandelion.DandelionProvider
import com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.standfordnlp.StandfordNlpProvider
import com.rafaelgarrote.utad.twitterstreaming.spark.SparkContextBuilder
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.spark.streaming.twitter.TwitterUtils

object Main {

  def main(args: Array[String]): Unit = {

    lazy val conf = AppProperties.config
    val (sparkSession, ssc) = SparkContextBuilder.createSessionStreamingContexts(conf)
    implicit val session = sparkSession
    implicit val sqlContext = session.sqlContext
    ssc.checkpoint("/tmp/")

    val rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.ERROR)

    lazy val datasource = AppProperties.getDataSource
    lazy val options = AppProperties.getElasticserachPorpertiesAsMap

    val stream = TwitterUtils.createStream(ssc, twitterAuth = TwitterConfig.getAuthorizationFactoryInstance)
    val filteredTweets = stream.filter(_.getLang == "en").cache()

    import com.rafaelgarrote.utad.twitterstreaming.spark.GenericWriter._
    import sqlContext.implicits._
    filteredTweets.map(status =>
      AnalysisResult(status.getText,
        DandelionProvider.extractSentiment(status.getText).toOption.map(_.sentiment.`type`),
        DandelionProvider.extractEntities(status.getText).toOption.map(_.annotations.map(_.label))
          .getOrElse(List.empty[String])
      )
    ).foreachRDD(rdd => rdd.toDF().writeDF(datasource, options))

    filteredTweets.map(status =>
      AnalysisResult(status.getText,
          StandfordNlpProvider.extractSentiment(status.getText).toOption,
          StandfordNlpProvider.extractEntities(status.getText).toOption.map(_.map(_._2).toList)
            .getOrElse(List.empty[String]))
    ).foreachRDD(rdd => rdd.toDF().writeDF(datasource, options))

    ssc.start()
    ssc.awaitTermination()

  }
}
