package com.campspot
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.campspot.actors.SearchActor.{ SearchObject, SearchRange }
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val campsiteJsonFormat = jsonFormat2(Campsite)
  implicit val reservationJsonFormat = jsonFormat3(Reservation)
  implicit val searchRangeJsonFormat = jsonFormat2(SearchRange)
  implicit val campsiteListJsonFormat = jsonFormat1(Campsites)
  implicit val searchObjectJsonFormat = jsonFormat3(SearchObject)

}
