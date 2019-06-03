package com.campspot.actors

import akka.actor.{ Actor, ActorLogging, Props }
import com.campspot.{ Campsite, Campsites, DateSearch, Reservation }
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class SearchActor extends Actor with ActorLogging {
  import SearchActor._

  private def findAvailableCampsitesForDateRange(searchTrait: SearchTrait): Seq[Long] = {
    DateSearch.findAvailableCampsites(searchTrait)
  }

  private def lookupCampsiteById(ids: Seq[Long], searchObject: SearchObject): Campsites = {
    var idHashSet = new mutable.HashSet[Long]
    ids.foreach(id => idHashSet += id)

    val filteredCampsites = searchObject.campsites.filterNot(campsite => idHashSet.contains(campsite.id))
    Campsites(filteredCampsites)
  }

  private def composeRequestFromSearchObject(searchObject: Option[SearchObject]): Campsites = {
    if (searchObject.isDefined) {
      val campsiteIds = findAvailableCampsitesForDateRange(searchObject.get)
      return lookupCampsiteById(campsiteIds, searchObject.get)

    } else {
      log.info("Search Object is not defined. Check if file name exists")
      return new Campsites(new ArrayBuffer[Campsite])
    }

  }
  // This is where the message comes to the actor
  def receive: Receive = {
    case request: Option[SearchObject] =>
      sender() ! composeRequestFromSearchObject(request)
  }
}

object SearchActor {
  final case class SearchObject(search: SearchRange, campsites: Seq[Campsite], reservations: Seq[Reservation]) extends SearchTrait
  final case class SearchRange(startDate: String, endDate: String)

  //using a trait to loosely couple searching to this trait and not any class
  trait SearchTrait {
    def search: SearchRange
    def reservations: Seq[Reservation]
  }

  def props: Props = Props[SearchActor]
}