package com.campspot

case class Campsite(id: Long, name: String)
case class Campsites(campsites: Seq[Campsite])

case class Reservation(startDate: String, endDate: String, campsiteId: Long)