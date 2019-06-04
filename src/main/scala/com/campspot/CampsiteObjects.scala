package com.campspot
//can separate these out if there becomes a need to actually put some logic around these objects
case class Campsite(id: Long, name: String)
case class Campsites(campsites: Seq[Campsite])

case class Reservation(startDate: String, endDate: String, campsiteId: Long)