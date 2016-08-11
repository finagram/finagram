package ru.finagram.api

import org.json4s._
import org.json4s.native.JsonMethods._

/**
 * Response from Telegram on request.
 */
sealed trait Response {
  val ok: Boolean
}

object Response {
  implicit val formats = DefaultFormats + FieldSerializer[Response]()

  def apply(content: String): Response = {
    val json = parse(content).camelizeKeys
    val ok = (json \ "ok").extract[JBool].value
    if (ok) {
      json.extract[Updates]
    } else {
      json.extract[TelegramException]
    }
  }
}

/**
 * Description of the error.
 *
 * @param description A human-readable description of the result.
 * @param errorCode Contains a code of error
 */
case class TelegramException(description: String, errorCode: Option[Int])
  extends Exception(description + " error code: " + errorCode.getOrElse("")) with Response {
  val ok = false
}

/**
 * Successful result that contains updates from Telegram.
 *
 * @param result updates from Telegram.
 */
case class Updates(result: Seq[Update]) extends Response {
  val ok = true
}

/**
 * This object represents an incoming update.
 *
 * @param updateId  The update‘s unique identifier.
 * @param message   Update identifiers start from a certain positive number and increase sequentially.
 *                  New incoming message of any kind — text, photo, sticker, etc.
 */
case class Update(updateId: Long, message: Option[Message])