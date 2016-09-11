package ru.finagram.api

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

sealed trait KeyboardMarkup

/**
 * This object represents a custom keyboard with reply options.
 *
 * @param keyboard Array of button rows, each represented by an Array of [[KeyboardButton]] objects.
 * @param resizeKeyboard Requests clients to resize the keyboard vertically for optimal fit.
 * @param oneTimeKeyboard Requests clients to hide the keyboard as soon as it's been used.
 * @param selective Use this parameter if you want to show the keyboard to specific users only.
 */
case class ReplyKeyboardMarkup(
  keyboard: Seq[Seq[KeyboardButton]],
  resizeKeyboard: Option[Boolean] = None,
  oneTimeKeyboard: Option[Boolean] = None,
  selective: Option[Boolean] = None
) extends KeyboardMarkup

/**
 * This object represents one button of the reply keyboard.
 *
 * @param text 	          Text of the button. If none of the optional fields are used,
 *                        it will be sent to the bot as a message when the button is pressed.
 * @param requestContact  If True, the user's phone number will be sent as a contact
 *                        when the button is pressed. Available in private chats only.
 * @param requestLocation If True, the user's current location will be sent when the button is pressed.
 *                        Available in private chats only.
 */
case class KeyboardButton(
  text: String,
  requestContact: Option[Boolean] = None,
  requestLocation: Option[Boolean] = None
)

/**
 * This object represents an inline keyboard that appears right next to the message it belongs to.
 *
 * @param inlineKeyboard Array of button rows, each represented by an Array of
 *                       [[InlineKeyboardButton]] objects.
 */
case class InlineKeyboardMarkup(inlineKeyboard: Seq[Seq[InlineKeyboardButton]]) extends KeyboardMarkup

/**
 * This trait represents one button of an inline keyboard.
 */
trait InlineKeyboardButton {
  val text: String
  val switchInlineQuery: Option[String]
}

/**
 * This object represents one button of an inline keyboard.
 *
 * @param text Label text on the button
 * @param callbackData Data to be sent in a callback query to the bot when button is pressed, 1-64 bytes
 * @param switchInlineQuery If set, pressing the button will prompt the user to select one of their
 *                          chats, open that chat and insert the bot‘s username and the specified
 *                          inline query in the input field. Can be empty, in which case just
 *                          the bot’s username will be inserted.
 */
case class InlineCallbackKeyboardButton(
  text: String,
  callbackData: String,
  switchInlineQuery: Option[String] = None
) extends InlineKeyboardButton

/**
 * This object represents one button of an inline keyboard.
 *
 * @param text Label text on the button
 * @param url HTTP url to be opened when button is pressed
 * @param switchInlineQuery If set, pressing the button will prompt the user to select one of their
 *                          chats, open that chat and insert the bot‘s username and the specified
 *                          inline query in the input field. Can be empty, in which case just
 *                          the bot’s username will be inserted.
 */
case class InlineUrlKeyboardButton(
  text: String,
  url: String,
  switchInlineQuery: Option[String] = None
) extends InlineKeyboardButton


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

