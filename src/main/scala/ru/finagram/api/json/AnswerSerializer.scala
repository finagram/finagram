package ru.finagram.api.json

import org.json4s.JsonAST.{ JInt, JObject }
import org.json4s.JsonDSL._
import org.json4s.{ DefaultFormats, Extraction, Formats, JValue, Serializer, TypeInfo }
import ru.finagram.api._

object AnswerSerializer extends Serializer[Answer] {

  override def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Answer] = PartialFunction.empty

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
