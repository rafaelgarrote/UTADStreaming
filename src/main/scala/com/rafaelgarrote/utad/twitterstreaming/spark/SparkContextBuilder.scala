package com.rafaelgarrote.utad.twitterstreaming.spark

import com.typesafe.config.Config
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext

object SparkContextBuilder {

  def createSessionStreamingContexts(config: Config): (SparkSession, StreamingContext) = {
//    val seconds = config.getInt(ConfigConstants.SparkSeconds)
    val seconds = 10
    val sparkConf = getSparkConf
    val session = SparkSession.builder().config(sparkConf).getOrCreate()
    (session, new StreamingContext(session.sparkContext, Seconds(seconds)))
  }

  private def getSparkConf: SparkConf = //new SparkConf()
    new SparkConf().setAppName("Tweet Example").setMaster("local[2]")

}
