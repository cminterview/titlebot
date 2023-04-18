package com.eck

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import com.eck.Supervisor._
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors

class Routes(supervisor: ActorRef[Supervisor.Command])(implicit val system: ActorSystem[_]) {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  implicit private val timeout: Timeout =
    Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def getPageInfo(url: String): Future[GetPageInfoResponse] =
    supervisor.ask(GetPageInfo(url, _))

  val routes: Route = cors() {
    pathPrefix("title") {
      path(Segment) { url =>
        get {
          rejectEmptyResponse {
            onSuccess(getPageInfo(url)) { response =>
              complete(response.pageInfo)
            }
          }
        }
      }
    }
  }
}
