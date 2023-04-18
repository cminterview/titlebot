package com.eck

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout
import com.eck.database.MongoService

import scala.concurrent.duration.DurationInt

final case class PageInfo(
    url: String,
    title: String,
    faviconUrl: String,
  )

object Supervisor {

  sealed trait Command

  final case class GetPageInfo(name: String, replyTo: ActorRef[GetPageInfoResponse]) extends Command

  final case class GetPageInfoResponse(pageInfo: PageInfo) extends Command

  final case class FetchPageInfoResponse(
      url: String,
      maybePageInfo: Option[PageInfo],
      router: ActorRef[GetPageInfoResponse],
    ) extends Command

  final case class GeneratePageInfoResponse(pageInfo: PageInfo, router: ActorRef[GetPageInfoResponse]) extends Command

  def apply(): Behavior[Command] = {
    implicit val timeout: Timeout = 3.seconds
    Behaviors.setup[Command] { context =>
      val databaseActor     = context.spawn(Database(), "db-actor")
      val scraperActor      = context.spawn(Scraper(), "scraper-actor")
      implicit val database = MongoService()
      Behaviors.receiveMessage {
        case GetPageInfo(url, router)                          =>
          context.log.info(s"Received initial request for ${url}")
          databaseActor ! Database.RetrievePageInfo(s"https://${url}", database, context.self, router)
          Behaviors.same
        case FetchPageInfoResponse(url, maybePageInfo, router) =>
          context.log.info(s"Checking fetched response for URL: ${url}")
          maybePageInfo match {
            case Some(pageInfo) => router ! GetPageInfoResponse(pageInfo)
            case None           => scraperActor ! Scraper.GeneratePageInfo(url, context.self, databaseActor, database, router)
          }
          Behaviors.same
        case GeneratePageInfoResponse(pageInfo, router)        =>
          router ! GetPageInfoResponse(pageInfo)
          Behaviors.same
      }
    }
  }

}
