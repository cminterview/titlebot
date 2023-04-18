package com.eck.database

import com.eck.PageInfo
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.result.InsertOneResult
import org.mongodb.scala.{ MongoClient, MongoCollection }
import org.mongodb.scala.bson.codecs.Macros._
import org.bson.codecs.configuration.CodecRegistries.{ fromProviders, fromRegistries }
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

case class MongoService() {
  val codecRegistry                           = fromRegistries(fromProviders(classOf[DbPageInfo]), DEFAULT_CODEC_REGISTRY)
  val connectionString: String                = "mongodb://root:example@localhost/?authSource=admin"
  val mongoClient: MongoClient                = MongoClient(connectionString)
  val database                                = mongoClient.getDatabase("titlebot").withCodecRegistry(codecRegistry)
  val collection: MongoCollection[DbPageInfo] = database.getCollection("pages")

  def getPageInfo(url: String): Seq[DbPageInfo] = {
    val observable = collection.find(equal("url", url))
    Await.result(observable.toFuture(), 5.seconds)
  }

  def addPageInfo(pageInfo: PageInfo): InsertOneResult = {
    val observable = collection.insertOne(DbPageInfo(pageInfo))
    Await.result(observable.toFuture(), 5.seconds)
  }

}
