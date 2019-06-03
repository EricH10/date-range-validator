package com.example

//#user-routes-spec
//#test-top
import akka.actor.{ ActorRef, Props }
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.campspot.actors.{ FileParserActor, SearchActor }
import com.campspot.{ Campsites, Routes }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ Matchers, WordSpec }

//#set-up
class RoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
  with Routes {
  //#test-top

  // Here we need to implement all the abstract members of Routes.
  // We use the real SearchActor to test it while we hit the Routes,
  // but we could "mock" it by implementing it in-place or by using a TestProbe()
  override val searchActor: ActorRef =
    system.actorOf(Props[SearchActor], "searchActor")

  override val fileParserActor: ActorRef =
    system.actorOf(Props[FileParserActor], "parserActor")

  lazy val routes = userRoutes

  //#set-up

  //#actual-test
  "Routes" should {
    "return 3 campsites if present (GET /search)" in {
      // note that there's no need for the host part in the uri:
      val request = HttpRequest(uri = "/search?filename=test-case.json")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        // and no entries should be in the list:
        entityAs[Campsites].campsites.size should ===(3)
      }
    }
  }

  "return 5 campsites if present (GET /search)" in {
    // note that there's no need for the host part in the uri:
    val request = HttpRequest(uri = "/search?filename=test-case-partially-empty.json")

    request ~> routes ~> check {
      status should ===(StatusCodes.OK)

      // we expect the response to be json:
      contentType should ===(ContentTypes.`application/json`)

      // and no entries should be in the list:
      entityAs[Campsites].campsites.size should ===(5)
    }
  }

  "return no campsites if no file exists (GET /search)" in {
    // note that there's no need for the host part in the uri:
    val request = HttpRequest(uri = "/search?filename=doesnt-exist")

    request ~> routes ~> check {
      status should ===(StatusCodes.OK)

      // we expect the response to be json:
      contentType should ===(ContentTypes.`application/json`)

      // and no entries should be in the list:
      entityAs[Campsites].campsites.size should ===(0)
    }
  }
}
