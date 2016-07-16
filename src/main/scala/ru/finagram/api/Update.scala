package ru.finagram.api

import org.json4s._
import org.json4s.native.JsonMethods._

/**
 * This object represents an incoming update.
 *
 * @param updateId  The update‘s unique identifier.
 * @param message   Update identifiers start from a certain positive number and increase sequentially.
 *                  New incoming message of any kind — text, photo, sticker, etc.
 */
private[finagram] case class Update(updateId: Long, message: Option[Message])

private[finagram] object Update {
  implicit val formats = DefaultFormats

  def apply(content: String): Option[Update] = {
    val json = parse(content).camelizeKeys
    val result = (json \ "result").extract[JArray].arr
    result.headOption match {
      case Some(update) =>
        val updateId = (update \ "updateId").extract[Long]
        val message = update \ "message" match {
          case msg: JObject =>
            Some(Message(msg))
          case _ =>
            None
        }
        Some(new Update(updateId, message))
      case None =>
        None
    }
  }
}