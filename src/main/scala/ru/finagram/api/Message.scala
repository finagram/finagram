package ru.finagram.api

import org.json4s.{ DefaultFormats, Formats, JValue, Serializer, TypeInfo }
import org.json4s.JsonAST.JObject

/**
 * This trait represents a message.
 */
sealed trait Message {
  val messageId: Long
  val from: Option[User]
  val chat: Chat
  val date: Long
}

object Message {
  implicit val formats = DefaultFormats

  def apply(json: JObject): Message = {
    json.values match {
      case v if v.contains("text") => json.extract[TextMessage]
      case v if v.contains("sticker") => json.extract[StickerMessage]
      case _ => ???
    }
  }
}

/**
 * This class represents a Telegram user or bot.
 *
 * @param id		      Unique identifier for this user or bot.
 * @param firstName		User‘s or bot’s first name.
 * @param lastName	  User‘s or bot’s last name.
 * @param username	  User‘s or bot’s username.
 */
case class User(id: Int, firstName: String, lastName: Option[String], username: Option[String])

/**
 * This class represents a chat.
 *
 * @param id        Unique identifier for this chat.
 * @param type      Type of chat, can be either “private”, “group”, “supergroup” or “channel”.
 * @param title     Optional. Title, for channels and group chats.
 * @param username  Optional. Username, for private chats, supergroups and channels if available.
 * @param firstName Optional. First name of the other party in a private chat.
 * @param lastName  Optional. Last name of the other party in a private chat.
 */
case class Chat(
  id: Long,
  `type`: String,
  title: Option[String] = None,
  firstName: Option[String] = None,
  lastName: Option[String] = None,
  username: Option[String] = None
)

/**
 * This class represents one size of a photo or a file / sticker thumbnail.
 *
 * @param fileId Unique identifier for this file.
 * @param width Photo width.
 * @param height Photo height.
 * @param fileSize File size.
 */
case class PhotoSize(fileId: String, width: Int, height: Int, fileSize: Option[Int])

/**
 * This class represents a sticker.
 *
 * @param fileId Unique identifier for this file.
 * @param width Sticker width.
 * @param height Sticker height.
 * @param thumb Sticker thumbnail in .webp or .jpg format.
 * @param emoji Emoji associated with the sticker.
 * @param fileSize File size.
 */
case class Sticker(fileId: String, width: Int, height: Int, thumb: Option[PhotoSize], emoji: Option[String], fileSize: Option[Int])

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
 */
case class StickerMessage(messageId: Long, from: Option[User], date: Long, chat: Chat, sticker: Sticker) extends Message
