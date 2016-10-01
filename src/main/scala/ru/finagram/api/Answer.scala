package ru.finagram.api

import org.json4s.{ DefaultFormats, Extraction, FieldSerializer, _ }

/**
 * Trait that represents answers from Bot to user.
 */
sealed trait Answer {

  /**
   * Unique identifier for the target chat or username of the target channel (in the format @channelusername)
   */
  val chatId: Long

  /**
   * Sends the message silently. iOS users will not receive a notification,
   * Android users will receive a notification with no sound.
   */
  val disableNotification: Option[Boolean]

  /**
   * If the message is a reply, ID of the original message
   */
  val replyToMessageId: Option[Long]

  /**
   * A custom keyboard with reply options.
   */
  val replyMarkup: Option[KeyboardMarkup]
}

object Answer {
  implicit val formats = DefaultFormats + FieldSerializer[Answer](FieldSerializer.ignore("content"))

  def serialize(answer: Answer): JValue = {
    Extraction.decompose(answer).snakizeKeys
  }
}

trait TextAnswer extends Answer {

  /**
   * Text of the message to be sent
   */
  final val text: String = {
    if (content.length > 4096)
      throw ContentIsTooLongException(content.length)
    content
  }

  /**
   * Disables link previews for links in this message
   */
  val disableWebPagePreview: Option[Boolean]

  /**
   * The actual UTF-8 text of the message, 0-4096 characters.
   */
  protected val content: String
}

/**
 * Simple text answer without formatting.
 *
 * @param chatId Identifier for the target chat or username of the target channel (in the format @channelusername)
 * @param content Text of the message to be sent
 * @param replyMarkup A custom keyboard with reply options.
 * @param disableWebPagePreview Disables link previews for links in this message
 * @param disableNotification Sends the message silently. iOS users will not receive a notification, Android users will receive a notification with no sound.
 * @param replyToMessageId If the message is a reply, ID of the original message
 */
case class FlatAnswer(
  chatId: Long,
  content: String,
  replyMarkup: Option[KeyboardMarkup] = None,
  disableWebPagePreview: Option[Boolean] = None,
  disableNotification: Option[Boolean] = None,
  replyToMessageId: Option[Long] = None) extends TextAnswer {
}

/**
 * @inheritdoc
 */
case class MarkdownAnswer(
  chatId: Long,
  content: String,
  replyMarkup: Option[KeyboardMarkup] = None,
  disableWebPagePreview: Option[Boolean] = None,
  disableNotification: Option[Boolean] = None,
  replyToMessageId: Option[Long] = None,
  final val parseMode: String = "Markdown") extends TextAnswer

/**
 * @inheritdoc
 */
case class HtmlAnswer(
  chatId: Long,
  content: String,
  replyMarkup: Option[KeyboardMarkup] = None,
  disableWebPagePreview: Option[Boolean] = None,
  disableNotification: Option[Boolean] = None,
  replyToMessageId: Option[Long] = None,
  final val parseMode: String = "HTML") extends TextAnswer

/**
 * Answer with image.
 *
 * @param photo Photo to send. You can either pass a file_id as String to resend a photo that is already on the Telegram servers.
 * @param caption Photo caption (may also be used when resending photos by file_id), 0-200 characters.
 */
case class PhotoAnswer(
  chatId: Long,
  photo: String, // TODO: add InputFile support
  caption: Option[String],
  replyMarkup: Option[KeyboardMarkup] = None,
  disableNotification: Option[Boolean] = None,
  replyToMessageId: Option[Long] = None
) extends Answer

/**
 * Answer with sticker.
 *
 * @param sticker Sticker to send. You can either pass a file_id as String to resend a sticker
 *                that is already on the Telegram servers.
 */
case class StickerAnswer(
  chatId: Long,
  sticker: String, // TODO: add InputFile support
  replyMarkup: Option[KeyboardMarkup] = None,
  disableNotification: Option[Boolean] = None,
  replyToMessageId: Option[Long] = None
) extends Answer

