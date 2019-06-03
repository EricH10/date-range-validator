package com.campspot.actors

import akka.actor.{ Actor, ActorLogging, Props }
import com.campspot.JsonSupport
import com.campspot.actors.SearchActor.SearchObject
import spray.json.JsonParser

import scala.util.Try

class FileParserActor extends Actor with ActorLogging with JsonSupport {
  import FileParserActor._

  // This is where the message comes to the actor
  def receive: Receive = {
    case request: ParseFile =>
      sender() ! Try(parseFile(request.fileName)).toOption
  }

  def parseFile(fileName: String): SearchObject = {
    val fileReader = io.Source.fromFile(fileName)
    val searchObject = JsonParser(fileReader.mkString).asJsObject.convertTo[SearchObject]
    fileReader.close()
    searchObject
  }
}

object FileParserActor {
  final case class ParseFile(fileName: String)

  def props: Props = Props[FileParserActor]
}