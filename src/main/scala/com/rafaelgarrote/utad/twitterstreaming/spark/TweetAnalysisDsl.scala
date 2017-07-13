package com.rafaelgarrote.utad.twitterstreaming.spark

import com.rafaelgarrote.utad.twitterstreaming.conf.AppProperties
import com.rafaelgarrote.utad.twitterstreaming.model.AnalysisResult
import com.rafaelgarrote.utad.twitterstreaming.model.Author
import com.rafaelgarrote.utad.twitterstreaming.model.EnrichedTweet
import com.rafaelgarrote.utad.twitterstreaming.model.RTEdge
import com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.dandelion.DandelionProvider
import com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.standfordnlp.StandfordNlpProvider
import com.rafaelgarrote.utad.twitterstreaming.utils
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.DStream
import org.neo4j.spark.Neo4j
import twitter4j.Status

class StatusAnalysisFunctions(self: DStream[Status])
                       (implicit session: SparkSession) {

  def analyzeTweet: DStream[AnalysisResult] = {
    self.map(status =>
      AnalysisResult(status,
        StandfordNlpProvider.extractSentiment(status.getText).toOption,
        DandelionProvider.extractEntities(status.getText).toOption.map(_.annotations.map(_.label))
          .getOrElse(List.empty[String])
      )
    )
  }

}

class TweetAnalysisFunctions(self: DStream[AnalysisResult])
                            (implicit session: SparkSession, neo: Neo4j) extends Serializable {

  lazy val datasource: String = AppProperties.getDataSource
  lazy val options: Map[String, String] = AppProperties.getElasticserachPorpertiesAsMap

  def enrichTweetAndPersist: Unit = {
    import com.rafaelgarrote.utad.twitterstreaming.spark.GenericWriter._
    import session.sqlContext.implicits._

    self.filter(!_.status.isRetweet).map{ status =>
      val tweetEntities = utils.TwitterUtils.extractTweetEntities(status.status)
      EnrichedTweet(
        id = status.status.getId,
        author = Author(
          id = status.status.getUser.getId,
          screenName = Some(status.status.getUser.getScreenName),
          name = status.status.getUser.getName,
          location = Some(status.status.getUser.getLocation)
        ),
        text = status.status.getText,
        date = status.status.getCreatedAt.getTime,
        mentions = tweetEntities._1.map(_.getId),
        hashTags = tweetEntities._2.map(_.getText),
        urls = tweetEntities._3.map(_.getURL),
        isRT = status.status.isRetweet,
        favoriteCount = status.status.getFavoriteCount,
        RTCount = status.status.getRetweetCount,
        sentiment = status.sentiment,
        entities = status.entities
      )
    }.foreachRDD(rdd => rdd.toDF().writeDF(datasource, options))
  }

  def crawlRTsAndPersist: Unit = {
    import com.rafaelgarrote.utad.twitterstreaming.spark.GenericWriter._
    import session.sqlContext.implicits._

    self.filter(_.status.isRetweet).map{ status =>
      val author = status.status.getRetweetedStatus.getUser
      val rtBy = status.status.getUser
      RTEdge(
        authorName = author.getName,
        authorScreenName = author.getScreenName,
        authorId = author.getId,
        tweetId = status.status.getRetweetedStatus.getId,
        rtByName = rtBy.getName,
        rtByScreenName = rtBy.getScreenName,
        rtById = rtBy.getId,
        sentiment = status.sentiment.get
      )
    }.foreachRDD(rdd => rdd.toDF.writeNeo4jDF)
  }

}

trait TweetAnalysisDsl {

  implicit def analysisFunctions(dStream: DStream[Status])
                                (implicit session: SparkSession) =
    new StatusAnalysisFunctions(dStream)

  implicit def tweetAnalysisFunctions(dStream: DStream[AnalysisResult])
                                     (implicit session: SparkSession,
                                      neo: Neo4j) =
    new TweetAnalysisFunctions(dStream)
}

object TweetAnalysisDsl extends TweetAnalysisDsl
