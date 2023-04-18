package com.eck

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout
import com.eck.Supervisor.GetPageInfoResponse
import com.eck.database.MongoService
import scala.concurrent.duration.DurationInt

object Database {
  sealed trait Command

  final case class RetrievePageInfo(url: String, database: MongoService, replyTo: ActorRef[Supervisor.Command], router: ActorRef[GetPageInfoResponse]) extends Command

  final case class StorePageInfo(pageInfo: PageInfo, database: MongoService) extends Command

  def apply(): Behavior[Command] =
    Behaviors.setup[Command] { context =>
      implicit val timeout: Timeout = 30.seconds
      Behaviors.receiveMessage {
        case RetrievePageInfo(url, db, replyTo, router) =>
          context.log.info("retrieving from db")
          val pageInfo: Option[PageInfo] = db.getPageInfo(url) match {
            case Seq() => None
            case Seq(dbPageInfo) => Some(dbPageInfo.toPageInfo)
            case Seq(first, _@_ *) => Some(first.toPageInfo)
          }
          replyTo ! Supervisor.FetchPageInfoResponse(url, pageInfo, router)
          Behaviors.same
        case StorePageInfo(pageInfo, db) =>
          db.addPageInfo(pageInfo)
          Behaviors.same
      }
    }
}
