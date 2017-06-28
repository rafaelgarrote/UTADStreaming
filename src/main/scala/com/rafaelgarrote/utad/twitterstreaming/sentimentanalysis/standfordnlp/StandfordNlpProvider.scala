package com.rafaelgarrote.utad.twitterstreaming.sentimentanalysis.standfordnlp

import java.util.Properties

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import edu.stanford.nlp.util.CoreMap

class StandfordNlpProvider {

  def extractSentiment(text: String): String = {
    val props = new Properties()
    props.setProperty("annotators", "tokenize, ssplit, parse, sentiment")
    val pipeline = new StanfordCoreNLP(props)

    val annotation = new Annotation(tweet)
    pipeline.annotate(annotation)

    val sentences = annotation.get(classOf[CoreAnnotations.SentencesAnnotation])
    val sentence: CoreMap = sentences.get(0)
    val sentiment = sentence.get(classOf[SentimentCoreAnnotations.SentimentClass])
    sentiment
  }

}
