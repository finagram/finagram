package ru.finagram.api.json

import org.json4s.JsonAST.JObject
import org.json4s.{ Formats, JValue, Serializer, TypeInfo }
import ru.finagram.api.{ InlineKeyboardMarkup, KeyboardMarkup, ReplyKeyboardMarkup }

object KeyboardMarkupSerializer extends Serializer[KeyboardMarkup] {

  private val KeyboardMarkupClass = classOf[KeyboardMarkup]

  override def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), KeyboardMarkup] = {
    case (TypeInfo(KeyboardMarkupClass, _), json: JObject) =>
      json.values match {
        case v if v.contains("keyboard") =>
          json.extract[ReplyKeyboardMarkup]
        case v if v.contains("inline_keyboard") =>
          json.extract[InlineKeyboardMarkup]
      }
  }

  override def serialize(implicit format: Formats): PartialFunction[Any, JValue] = PartialFunction.empty
}
