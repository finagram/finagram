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
  implicit val formats = DefaultFormats

  def apply(content: String): Response = {
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
  extends Exception(description + " error code: " + errorCode.getOrElse("")) with Response {
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
case class Updates(result: Seq[Update]) extends Response {
  val ok = true
}

object Update {

  def apply(update: JValue): Update = ???
}

object Updates {

  def apply(result: List[JValue]): Updates = {
    apply(result.map(Update.apply))
  }
}

class UpdatesSerializer extends Serializer[Updates] {
  private val UpdatesClass = classOf[Updates]

  override def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Updates] = {
    case (TypeInfo(UpdatesClass, _), json) => json match {
      case JObject(JField("result", JArray(result)) :: _) =>
        Updates(result)
      case x =>
        throw new MappingException("Can't convert " + x + s" to $UpdatesClass")
    }
  }

  override def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case updates: Updates => ???
  }
}