package ru.finagram.api.json

import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.{ DefaultFormats, Extraction, Formats, JValue, Serializer, TypeInfo }
import ru.finagram.api._
import ru.finagram.!!!

object MessageSerializer extends Serializer[Message] {

  override def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Message] = PartialFunction.empty

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
