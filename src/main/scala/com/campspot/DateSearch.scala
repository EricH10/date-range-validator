package com.campspot
import java.time.LocalDate

import com.campspot.actors.SearchActor.SearchTrait

import scala.collection.mutable

object DateSearch {

  def findAvailableCampsites(searchObject: SearchTrait): Seq[Long] = {
    val startSearch = stringToDateConversion(searchObject.search.startDate)
    val endSearch = stringToDateConversion(searchObject.search.endDate)
    var campsiteIdsAvailable = new mutable.ArrayBuffer[Long]

    /* group existing reservations into groups based on campsite.
     Iterate over each campsites reservations and check if the search overlaps any reservation
     also check for the gap rule and if it violates that
    */
    searchObject.reservations
      .groupBy(reservation => reservation.campsiteId)
      .values
      .foreach(groupedReservations => {
        var isCampsiteAvailable = true
        val campsiteId = groupedReservations.head.campsiteId
        groupedReservations.foreach(reservation => {
          val reservedStartDate = stringToDateConversion(reservation.startDate)
          val reservedEndDate = stringToDateConversion(reservation.endDate)

          if (!isDateRangeValid(startSearch, endSearch, reservedStartDate, reservedEndDate)) {
            isCampsiteAvailable = false
          }
        })
        if (isCampsiteAvailable) campsiteIdsAvailable += campsiteId
      })
    campsiteIdsAvailable
  }

  private def stringToDateConversion(date: String): LocalDate = {
    LocalDate.parse(date)
  }

  private def isDateRangeValid(firstStart: LocalDate, firstEnd: LocalDate,
    secondStart: LocalDate, secondEnd: LocalDate): Boolean = {
    var isValid = false
    // could short circuit based on this result
    val satisfiesGapRule = checkGapRule(firstStart, firstEnd, secondStart, secondEnd)

    if (firstStart.isBefore(secondStart) && firstEnd.isBefore(secondStart) && satisfiesGapRule) isValid = true

    if (firstStart.isAfter(secondStart) && firstStart.isAfter(secondEnd) && satisfiesGapRule) isValid = true

    isValid
  }

  //checks gap rule by applying default factor for the gap which is 1
  //By default adds 2 to each end of reservation range and see if any dates are equal
  private def checkGapRule(firstStart: LocalDate, firstEnd: LocalDate,
    secondStart: LocalDate, secondEnd: LocalDate, factor: Long = 1): Boolean = {
    var satisfiesGapRule = true
    val adjustedFactor = factor + 1
    val searchTuple = (firstStart.minusDays(adjustedFactor), firstEnd.plusDays(adjustedFactor))

    if (searchTuple._1.isEqual(secondEnd) || searchTuple._2.isEqual(secondStart)) satisfiesGapRule = false

    satisfiesGapRule

  }

}