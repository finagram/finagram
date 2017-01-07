package ru.finagram.api

/**
 * This object represents an incoming callback query from a callback button in an inline keyboard.
 * If the button that originated the query was attached to a message sent by the bot,
 * the field message will be presented. If the button was attached to a message sent
 * via the bot (in inline mode), the field inline_message_id will be presented.
 *
 * @param id Unique identifier for this query.
 * @param from Sender.
 * @param data Data associated with the callback button.
 *             Be aware that a bad client can send arbitrary data in this field.
 * @param message Message with the callback button that originated the query.
 *                Note that message content and message date will not be available
 *                if the message is too old.
 * @param inlineMessageId Identifier of the message sent via the bot in inline mode,
 *                        that originated the query.
 */
case class CallbackQuery(
  id: String,
  from: User,
  data: String,
  message: Option[Message] = None,
  inlineMessageId: Option[String] = None
)
