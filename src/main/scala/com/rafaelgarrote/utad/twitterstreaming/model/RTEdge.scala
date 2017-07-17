package com.rafaelgarrote.utad.twitterstreaming.model

case class RTEdge(
                   authorName: String,
                   authorScreenName: String,
                   authorId: Long,
                   tweetId: Long,
                   sentiment: String,
                   rtByName: String,
                   rtByScreenName: String,
                   rtById: Long
                 )
