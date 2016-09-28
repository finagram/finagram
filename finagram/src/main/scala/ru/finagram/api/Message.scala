package ru.finagram.api

/**
 * This trait represents a message.
 */
sealed trait Message {
  val messageId: Long
  val from: Option[User]
  val chat: Chat
  val date: Long
}

/**
 * Text message.
 *
 * @param messageId	Integer	Unique message identifier.
 * @param from 	    Sender, can be empty for messages sent to channels.
 * @param date    	Date the message was sent in Unix time.
 * @param chat 	    Conversation the message belongs to.
 * @param text      The actual UTF-8 text of the message, 0-4096 characters.
 */
case class TextMessage(messageId: Long, from: Option[User], date: Long, chat: Chat, text: String) extends Message

/**
 * Sticker.
 *
 * @param messageId	Integer	Unique message identifier.
 * @param from 	    Sender, can be empty for messages sent to channels.
 * @param date    	Date the message was sent in Unix time.
 * @param chat 	    Conversation the message belongs to.
 * @param sticker   Information about the sticker.
 * @param text      The actual UTF-8 text of the message, 0-4096 characters.
 */
case class StickerMessage(messageId: Long, from: Option[User], date: Long, chat: Chat, sticker: Sticker) extends Message

case class DocumentMessage(messageId: Long, from: Option[User], date: Long, chat: Chat, document: Document) extends Message

case class LocationMessage(messageId: Long, from: Option[User], date: Long, chat: Chat, location: Location) extends Message

case class PhotoMessage(messageId: Long, from: Option[User], date: Long, chat: Chat, photo: List[PhotoSize], text: Option[String]) extends Message

case class VideoMessage(messageId: Long, from: Option[User], date: Long, chat: Chat, video: Video) extends Message

case class VoiceMessage(messageId: Long, from: Option[User], date: Long, chat: Chat, voice: Voice) extends Message
