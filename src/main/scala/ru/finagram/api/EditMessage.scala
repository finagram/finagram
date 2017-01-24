package ru.finagram.api

trait EditMessage {
  val chatId: Option[String]
  val messageId: Option[Int]
  val inlineMessageId: Option[String]
  val replyMarkup: Option[InlineKeyboardMarkup]
}

case class EditMessageReplyMarkup(
  chatId: Option[String],
  messageId: Option[Int],
  inlineMessageId: Option[String],
  replyMarkup: Option[InlineKeyboardMarkup]
) extends EditMessage
