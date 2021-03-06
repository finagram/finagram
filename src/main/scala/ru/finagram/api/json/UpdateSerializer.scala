package ru.finagram.api.json

import org.json4s.JsonAST.JObject
import org.json4s.JsonDSL._
import org.json4s.{ DefaultFormats, Extraction, Formats, JValue, Serializer, TypeInfo }
import ru.finagram.api.{ CallbackQueryUpdate, MessageUpdate, Update }
import ru.finagram.!!!

object UpdateSerializer extends Serializer[Update] {

  private val UpdateClass = classOf[Update]

  override def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Update] = {
    case (TypeInfo(UpdateClass, _), json: JObject) =>
      json.values match {
        case v if v.contains("message") =>
          json.extract[MessageUpdate]
        case v if v.contains("callbackQuery") =>
          json.extract[CallbackQueryUpdate]
        case v => !!!(s"Not implemented deserialization for $v")
      }
  }

  override def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case u: MessageUpdate =>
      ("update_id" -> u.updateId) ~~ ("message" -> json(u.message))
    case u: CallbackQueryUpdate =>
      ("update_id" -> u.updateId) ~~ ("callback_query" -> json(u.callbackQuery))
  }

  private def  json(obj: AnyRef): JValue = {
    implicit val formats = DefaultFormats
    Extraction.decompose(obj).snakizeKeys
  }
}
