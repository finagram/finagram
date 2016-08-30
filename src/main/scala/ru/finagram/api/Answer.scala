package ru.finagram.api

// TODO add support for reply_markup

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
  val replyMarkup: Option[ReplyKeyboardMarkup]
}

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
  requestLocation: Option[Boolean] = None)

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
)

trait TextAnswer extends Answer {
  /**
   * Text of the message to be sent
   */
  val text: String

  /**
   * Disables link previews for links in this message
   */
  val disableWebPagePreview: Option[Boolean]
}

/**
 * Simple text answer without formatting.
 *
 * @param chatId Identifier for the target chat or username of the target channel (in the format @channelusername)
 * @param text Text of the message to be sent
 * @param replyMarkup A custom keyboard with reply options.
 * @param disableWebPagePreview Disables link previews for links in this message
 * @param disableNotification Sends the message silently. iOS users will not receive a notification, Android users will receive a notification with no sound.
 * @param replyToMessageId If the message is a reply, ID of the original message
 */
case class FlatAnswer(
  chatId: Long,
  text: String,
  replyMarkup: Option[ReplyKeyboardMarkup] = None,
  disableWebPagePreview: Option[Boolean] = None,
  disableNotification: Option[Boolean] = None,
  replyToMessageId: Option[Long] = None) extends TextAnswer

/**
 * @inheritdoc
 */
case class MarkdownAnswer(
  chatId: Long,
  text: String,
  replyMarkup: Option[ReplyKeyboardMarkup] = None,
  disableWebPagePreview: Option[Boolean] = None,
  disableNotification: Option[Boolean] = None,
  replyToMessageId: Option[Long] = None,
  final val parseMode: String = "Markdown") extends TextAnswer

/**
 * @inheritdoc
 */
case class HtmlAnswer(
  chatId: Long,
  text: String,
  replyMarkup: Option[ReplyKeyboardMarkup] = None,
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
  replyMarkup: Option[ReplyKeyboardMarkup] = None,
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
  replyMarkup: Option[ReplyKeyboardMarkup] = None,
  disableNotification: Option[Boolean] = None,
  replyToMessageId: Option[Long] = None
) extends Answer

