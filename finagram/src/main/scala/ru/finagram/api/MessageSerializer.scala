package ru.finagram.api

import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.{ DefaultFormats, Extraction, Formats, JValue, Serializer, TypeInfo }

object MessageSerializer extends Serializer[Message] {

  private val MessageClass = classOf[Message]

  override def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Message] = {
    case (TypeInfo(MessageClass, _), json: JObject) =>
      json.values match {
        case v if v.contains("document") =>
          json.extract[DocumentMessage]
        case v if v.contains("sticker") =>
          json.extract[StickerMessage]
        case v if v.contains("text") =>
          json.extract[TextMessage]
        case _ =>
          ???
      }
  }

  override def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case m: DocumentMessage =>
      JMessage(m) ~~ ("document" -> json(m.document))
    case m: StickerMessage =>
      JMessage(m) ~~ ("sticker" -> json(m.sticker))
    case m: TextMessage =>
      JMessage(m) ~~ ("text" -> m.text)
  }

  private def JMessage(m: Message): JObject = {
    val jobject = ("messageId" -> m.messageId) ~ ("chat" -> json(m.chat)) ~ ("date" -> m.date)
    if (m.from.isEmpty) jobject else jobject ~~ ("from", json(m.from.get))
  }

  private def  json(obj: AnyRef): JValue = {
    implicit val formats = DefaultFormats
    Extraction.decompose(obj)
  }
}
