package com.campspot

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.{ ask }

import scala.concurrent.{ Future }
import akka.util.Timeout
import com.campspot.actors.FileParserActor.ParseFile
import com.campspot.actors.SearchActor.SearchObject

trait Routes extends JsonSupport {

  // we leave these abstract, since they will be provided by the App
  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[Routes])
  def searchActor: ActorRef
  def fileParserActor: ActorRef

  // Required by the `ask` (?) method below
  implicit lazy val timeout = Timeout(5.seconds)

  lazy val userRoutes: Route =
    pathPrefix("search") {
      concat(
        pathEnd {
          concat(
            get {
              parameters('filename.as[String]) { (filename) =>
                val searchObject: Future[Option[SearchObject]] =
                  (fileParserActor ? ParseFile(filename)).mapTo[Option[SearchObject]]

                val result = searchObject map { searchObject =>
                  (searchActor ? searchObject).mapTo[Campsites]
                }
                complete(result)
              }
            })
        })
    }
}
