package ru.finagram.api.json

import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.{ DefaultFormats, Extraction, Formats, JValue, Serializer, TypeInfo }
import ru.finagram.api._

object MessageSerializer extends Serializer[Message] {

  private val MessageClass = classOf[Message]

  override def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Message] = {
    case (TypeInfo(MessageClass, _), json: JObject) =>
      json.values match {
        case v if v.contains("video") =>
          json.extract[VideoMessage]
        case v if v.contains("voice") =>
          json.extract[VoiceMessage]
        case v if v.contains("photo") =>
          json.extract[PhotoMessage]
        case v if v.contains("location") =>
          json.extract[LocationMessage]
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
    case m: VideoMessage =>
      JMessage(m) ~~ ("video" -> json(m.video))
    case m: VoiceMessage =>
      JMessage(m) ~~ ("voice" -> json(m.voice))
    case m: PhotoMessage =>
      JMessage(m) ~~ ("photo" -> json(m.photo))
    case m: LocationMessage =>
      JMessage(m) ~~ ("location" -> json(m.location))
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
