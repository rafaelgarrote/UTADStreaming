package com.rafaelgarrote.utad.twitterstreaming.spark

import com.rafaelgarrote.utad.twitterstreaming.conf.AppProperties
import com.rafaelgarrote.utad.twitterstreaming.model.AnalysisResult
import com.rafaelgarrote.utad.twitterstreaming.model.Author
import com.rafaelgarrote.utad.twitterstreaming.model.EnrichedTweet
import com.rafaelgarrote.utad.twitterstreaming.model.RTEdge
import com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.dandelion.DandelionProvider
import com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.standfordnlp.StandfordNlpProvider
import com.rafaelgarrote.utad.twitterstreaming.utils
import com.rafaelgarrote.utad.twitterstreaming.utils.TwitterUtils
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.DStream
import org.neo4j.spark.Neo4j
import twitter4j.Status

class StatusAnalysisFunctions(self: DStream[Status])
                       (implicit session: SparkSession) {

  def extractEntitiesAndMentions: DStream[AnalysisResult] = {
    self.map { status =>
      AnalysisResult(
        status = status,
        entities = DandelionProvider.extractEntities(status.getText)
          .toOption.map(_.annotations.map(_.label)).getOrElse(List.empty[String]),
        mentions = TwitterUtils.extractTweetEntities(status)._1.map(_.getScreenName)
      )
    }
  }

}

class TweetAnalysisFunctions(self: DStream[AnalysisResult])
                            (implicit session: SparkSession, neo: Neo4j, bEntities: Broadcast[List[String]]) extends Serializable {

  def filterTweetsByEntities: DStream[AnalysisResult] = {
    val entities: List[String] = bEntities.value
    self.filter { result =>
      (result.entities ++ result.mentions).map(
        e => entities.contains(e.toLowerCase)
      ).foldLeft(false)(_ || _)
    }
  }

  def analyzeTweet: DStream[AnalysisResult] = {
    self.map( result =>
      result.copy(
        sentiment = StandfordNlpProvider.extractSentiment(result.status.getText).toOption)
    )
  }

  def enrichTweet: DStream[EnrichedTweet] = {
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
        mentions = tweetEntities._1.map(_.getScreenName),
        hashTags = tweetEntities._2.map(_.getText),
        urls = tweetEntities._3.map(_.getURL),
        isRT = status.status.isRetweet,
        favoriteCount = status.status.getFavoriteCount,
        RTCount = status.status.getRetweetCount,
        sentiment = status.sentiment,
        entities = status.entities
      )
    }
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

class EnrichedTweetFunctions(self: DStream[EnrichedTweet])
                            (implicit session: SparkSession) extends Serializable {

  lazy val datasource: String = AppProperties.getDataSource
  lazy val options: Map[String, String] = AppProperties.getElasticserachPorpertiesAsMap

  def persistElasticsearch: Unit = {
    import com.rafaelgarrote.utad.twitterstreaming.spark.GenericWriter._
    import session.sqlContext.implicits._
    self.print()
    self.foreachRDD(rdd => rdd.toDF().writeDF(datasource, options))
  }

}

trait TweetAnalysisDsl {

  implicit def analysisFunctions(dStream: DStream[Status])
                                (implicit session: SparkSession) =
    new StatusAnalysisFunctions(dStream)

  implicit def tweetAnalysisFunctions(dStream: DStream[AnalysisResult])
                                     (implicit session: SparkSession, neo: Neo4j, bEntities: Broadcast[List[String]]) =
    new TweetAnalysisFunctions(dStream)

  implicit def enrichedTweetsFunctions(dStream: DStream[EnrichedTweet])
                                      (implicit session: SparkSession) =
    new EnrichedTweetFunctions(dStream)

}

object TweetAnalysisDsl extends TweetAnalysisDsl
