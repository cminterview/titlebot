package com.eck

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, Behavior }
import akka.util.Timeout
import com.eck.Supervisor.GetPageInfoResponse
import com.eck.database.MongoService
import net.ruippeixotog.scalascraper.browser.{ Browser, JsoupBrowser }
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{ attr, elementList, text }
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

import scala.concurrent.duration.DurationInt

object Scraper {
  sealed trait Command

  final case class GeneratePageInfo(
      url: String,
      replyTo: ActorRef[Supervisor.Command],
      databaseActor: ActorRef[Database.StorePageInfo],
      database: MongoService,
      router: ActorRef[GetPageInfoResponse],
    ) extends Command

  def apply(): Behavior[Command] =
    Behaviors.setup[Command] { context =>
      implicit val timeout: Timeout = 50.seconds
      Behaviors.receiveMessage {
        case GeneratePageInfo(url, replyTo, databaseActorRef, database, router) =>
          context.log.info(s"Generating page info for ${url}")
          val doc: Browser#DocumentType = JsoupBrowser().get(url)
          val title                     = doc >> text("title")
          val faviconUrl                = getFaviconUrl(doc)
          val pageInfo                  = PageInfo(url, title, faviconUrl)
          replyTo ! Supervisor.GeneratePageInfoResponse(pageInfo, router)
          databaseActorRef ! Database.StorePageInfo(pageInfo, database)
          Behaviors.same
      }
    }

  def getFaviconUrl(doc: Browser#DocumentType) = {
    val iconList     =
      (doc >> elementList("link[rel='shortcut icon']") >?> attr("href")) ++
        (doc >> elementList("link[rel='icon']") >?> attr("href"))
    val filteredList = iconList.filter(i => i.nonEmpty).map(i => i.get)
    if (filteredList.nonEmpty) filteredList.head
    else ""
  }
}
