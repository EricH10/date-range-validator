package com.campspot

import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.concurrent.duration.Duration
import scala.util.{ Failure, Success }
import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.campspot.actors.{ FileParserActor, SearchActor }

object QuickstartServer extends App with Routes {

  // set up ActorSystem and other dependencies here
  implicit val system: ActorSystem = ActorSystem("helloAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val searchActor: ActorRef = system.actorOf(Props[SearchActor], "SearchActor")
  val fileParserActor: ActorRef = system.actorOf(Props[FileParserActor], "FileParserActor")

  // from the Routes trait
  lazy val routes: Route = userRoutes

  //#http-server
  val serverBinding: Future[Http.ServerBinding] = Http().bindAndHandle(routes, "localhost", 8080)

  serverBinding.onComplete {
    case Success(bound) =>
      println(s"Server online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
    case Failure(e) =>
      Console.err.println(s"Server could not start!")
      e.printStackTrace()
      system.terminate()
  }

  Await.result(system.whenTerminated, Duration.Inf)
}

