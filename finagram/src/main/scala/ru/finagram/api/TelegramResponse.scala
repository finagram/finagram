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
  implicit val formats = DefaultFormats + UpdateSerializer + MessageSerializer

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
 * Successful result that contains updates from Telegram.
 *
 * @param result updates from Telegram.
 */
case class Updates(result: Update*) extends TelegramResponse {
  val ok = true
}

