package ru.finagram.api

import org.json4s.{ DefaultFormats, Extraction, FieldSerializer, JValue }

trait EditMessage {
  val chatId: Option[String]
  val messageId: Option[Int]
  val inlineMessageId: Option[String]
  val replyMarkup: Option[InlineKeyboardMarkup]
}

object EditMessage {
  implicit val formats = DefaultFormats

  def serialize(answer: EditMessage): JValue = {
    Extraction.decompose(answer).snakizeKeys
  }
}

case class EditMessageReplyMarkup(
  chatId: Option[String],
  messageId: Option[Int],
  inlineMessageId: Option[String],
  replyMarkup: Option[InlineKeyboardMarkup]
) extends EditMessage
