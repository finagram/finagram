package ru.finagram.api

/**
 * Response from Telegram on request.
 */
sealed trait TelegramResponse {
  val ok: Boolean
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
case class Updates(result: Seq[Update]) extends TelegramResponse {
  val ok = true
}

case class FileResponse(result: File) extends TelegramResponse {
  val ok = true
}

case class MeResponse(result: User) extends TelegramResponse {
  val ok = true
}

