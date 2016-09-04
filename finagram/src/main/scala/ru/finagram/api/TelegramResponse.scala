package ru.finagram.api

import org.json4s._
import org.json4s.native.JsonMethods._

/**
 * Response from Telegram on request.
 */
sealed trait TelegramResponse {
  val ok: Boolean
}

object TelegramResponse {
  implicit val formats = DefaultFormats + MessageSerializer

  def apply(content: String): TelegramResponse = {
    val json = parse(content).camelizeKeys
    val ok = (json \ "ok").extract[Boolean]
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
  extends Exception(description + " error code: " + errorCode.getOrElse("")) with TelegramResponse {
  val ok = false
}

/**
 * This object represents an incoming update.
 *
 * @param updateId  The update‘s unique identifier.
 * @param message   Update identifiers start from a certain positive number and increase sequentially.
 *                  New incoming message of any kind — text, photo, sticker, etc.
 */
case class Update(updateId: Long, message: Option[Message])

/**
 * Successful result that contains updates from Telegram.
 *
 * @param result updates from Telegram.
 */
case class Updates(result: Seq[Update]) extends TelegramResponse {
  val ok = true
}