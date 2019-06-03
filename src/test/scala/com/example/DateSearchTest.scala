package com.example

import com.campspot.actors.SearchActor.{ SearchObject, SearchRange }
import com.campspot.{ DateSearch, Reservation }
import org.scalatest.{ Matchers, WordSpec }
import org.scalatest.concurrent.ScalaFutures

class DateSearchTest extends WordSpec with Matchers with ScalaFutures {
  def buildDataForSearch(start: String, end: String): SearchObject = {
    val reservations = createReservations()
    val searchRange = new SearchRange(start, end)
    SearchObject(searchRange, null, reservations)
  }

  def createReservations(): Seq[Reservation] = {
    val reservations = List(
      Reservation("2018-06-01", "2018-06-03", 1),
      Reservation("2018-06-08", "2018-06-10", 1),
      Reservation("2018-06-01", "2018-06-01", 2),
      Reservation("2018-06-02", "2018-06-03", 2),
      Reservation("2018-06-07", "2018-06-09", 2),
      Reservation("2018-06-01", "2018-06-02", 3),
      Reservation("2018-06-08", "2018-06-09", 3),
      Reservation("2018-06-07", "2018-06-10", 4))
    reservations
  }

  "DateSearch" should {
    "return 2 campsites)" in {
      val searchObject = buildDataForSearch("2018-06-04", "2018-06-06")
      val result = DateSearch.findAvailableCampsites(searchObject)
      result.size should ===(2)
    }

    "return 1 id" in {
      val searchObject = buildDataForSearch("2018-06-03", "2018-06-06")
      val result = DateSearch.findAvailableCampsites(searchObject)
      result.size should ===(1)
    }

    "return 4 ids" in {
      val searchObject = buildDataForSearch("2018-06-20", "2018-06-21")
      val result = DateSearch.findAvailableCampsites(searchObject)
      result.size should ===(4)
    }
  }
}
