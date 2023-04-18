package com.eck.database

import com.eck.PageInfo
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.codecs.Macros._

import org.bson.codecs.configuration.CodecRegistries.{ fromProviders, fromRegistries }
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY

object DbPageInfo {

  def apply(pageInfo: PageInfo): DbPageInfo =
    DbPageInfo(new ObjectId(), pageInfo.url, pageInfo.title, pageInfo.faviconUrl)
}

case class DbPageInfo(
    _id: ObjectId,
    url: String,
    title: String,
    faviconUrl: String,
  ) {
  def toPageInfo = PageInfo(url, title, faviconUrl)
}
