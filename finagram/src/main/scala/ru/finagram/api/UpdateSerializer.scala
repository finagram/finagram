package ru.finagram.api

import org.json4s.JsonDSL._
import org.json4s.JsonAST.JObject
import org.json4s.{ DefaultFormats, Extraction, Formats, JValue, Serializer, TypeInfo }

object UpdateSerializer extends Serializer[Update] {

  private val UpdateClass = classOf[Update]

  override def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Update] = {
    case (TypeInfo(UpdateClass, _), json: JObject) =>
      json.values match {
        case v if v.contains("message") =>
          json.extract[MessageUpdate]
        case _ =>
          ???
      }
  }

  override def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case u: MessageUpdate =>
      ("update_id" -> u.updateId) ~~ ("message" -> json(u.message))
  }

  private def  json(obj: AnyRef): JValue = {
    implicit val formats = DefaultFormats
    Extraction.decompose(obj).snakizeKeys
  }
}
