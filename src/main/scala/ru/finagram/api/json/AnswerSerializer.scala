package ru.finagram.api.json

import org.json4s.JsonAST.{ JInt, JObject }
import org.json4s.JsonDSL._
import org.json4s.{ DefaultFormats, Extraction, Formats, JValue, Serializer, TypeInfo }
import ru.finagram.api._

object AnswerSerializer extends Serializer[Answer] {

  private val AnswerClass = classOf[Answer]

  override def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Answer] = {
    case (TypeInfo(AnswerClass, _), json: JObject) =>
      json.values match {
        case v if v.contains("text") && v("parse_mode") == "Markdown" =>
          json.extract[MarkdownAnswer]
        case v if v.contains("text") && v("parse_mode") == "HTML" =>
          json.extract[HtmlAnswer]
        case v if v.contains("text") =>
          json.extract[FlatAnswer]
        case v if v.contains("photo") =>
          json.extract[PhotoAnswer]
        case v if v.contains("sticker") =>
          json.extract[StickerAnswer]
      }
  }

  override def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case a: FlatAnswer =>
      JTextAnswer(a) ~~ ("text" -> a.text)
    case a: MarkdownAnswer =>
      JTextAnswer(a) ~~ ("text" -> a.text) ~~ ("parseMode" -> "Markdown")
    case a: HtmlAnswer =>
      JTextAnswer(a) ~~ ("text" -> a.text) ~~ ("parseMode" -> "HTML")

    case a: PhotoAnswer =>
      if (a.caption.isEmpty)
        JAnswer(a) ~~ ("photo" -> a.photo)
      else
        JAnswer(a) ~~ ("photo" -> a.photo) ~~ ("caption" -> a.caption.get)

    case a: StickerAnswer =>
      JAnswer(a) ~~ ("sticker" -> a.sticker)
  }

  private def JTextAnswer(a: TextAnswer): JObject = {
    if (a.disableWebPagePreview.isDefined) {
      JAnswer(a) ~~ ("disableWebPagePreview" -> a.disableWebPagePreview.get)
    } else {
      JAnswer(a)
    }
  }

  private def JAnswer(a: Answer): JObject = {
    var jobj = JObject("chatId" -> JInt(a.chatId))
    jobj = if (a.disableNotification.isEmpty) jobj else jobj ~~ ("disableNotification" -> a.disableNotification.get)
    jobj = if (a.replyMarkup.isEmpty) jobj else jobj ~~ ("replyMarkup" -> json(a.replyMarkup.get))
    jobj = if (a.replyToMessageId.isEmpty) jobj else jobj ~~ ("replyToMessageId" -> a.replyToMessageId.get)
    jobj
  }

  private def json(obj: AnyRef): JValue = {
    implicit val formats = DefaultFormats
    Extraction.decompose(obj)
  }
}
