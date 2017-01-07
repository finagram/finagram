package ru.finagram

package object api {

  /**
   * This class represents a Telegram user or bot.
   *
   * @param id		      Unique identifier for this user or bot.
   * @param firstName		User‘s or bot’s first name.
   * @param lastName	  User‘s or bot’s last name.
   * @param username	  User‘s or bot’s username.
   */
  case class User(id: Int, firstName: String, lastName: Option[String] = None, username: Option[String] = None)

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

  trait Binary {
    val fileId: String
  }

  /**
   * Sticker.
   *
   * @param fileId Unique identifier for this file.
   * @param width Sticker width.
   * @param height Sticker height.
   * @param thumb Sticker thumbnail in .webp or .jpg format.
   * @param emoji Emoji associated with the sticker.
   * @param fileSize File size.
   */
  case class Sticker(
    fileId: String,
    width: Int,
    height: Int,
    thumb: Option[PhotoSize],
    emoji: Option[String],
    fileSize: Option[Int]
  ) extends Binary

  /**
   * Document.
   *
   * @param fileId 	  Unique file identifier
   * @param thumb 	  Document thumbnail as defined by sender
   * @param fileName  Original filename as defined by sender
   * @param mimeType  MIME type of the file as defined by sender
   * @param fileSize  File size
   */
  case class Document(
    fileId: String,
    thumb: Option[PhotoSize] = None,
    fileName: Option[String] = None,
    mimeType: Option[String] = None,
    fileSize: Option[Int] = None
  ) extends Binary

  /**
   * Location.
   *
   * @param longitude  Longitude as defined by sender
   * @param latitude   Latitude as defined by sender
   */
  case class Location(
    longitude: Double,
    latitude: Double
  )

  /**
   * Video.
   *
   * @param fileId    Unique identifier for this file.
   * @param width     Video width as defined by sender.
   * @param height    Video height as defined by sender.
   * @param duration  Duration of the video in seconds as defined by sender.
   * @param thumb     Video thumbnail.
   * @param mimeType  Mime type of a file as defined by sender.
   * @param fileSize  File size.
   */
  case class Video(
    fileId: String,
    width: Int,
    height: Int,
    duration: Int,
    thumb: Option[PhotoSize] = None,
    mimeType: Option[String] = None,
    fileSize: Option[Int] = None
  ) extends Binary

  /**
   * Voice
   *
   * @param fileId    Unique identifier for this file.
   * @param duration  Duration of the audio in seconds as defined by sender.
   * @param mimeType  MIME type of the file as defined by sender.
   * @param fileSize  File size.
   */
  case class Voice(
    fileId: String,
    duration: Int,
    mimeType: Option[String],
    fileSize: Option[Int]
  ) extends Binary

  /**
   * File.
   *
   * @param fileId	  Unique identifier for this file
   * @param fileSize	File size, if known
   * @param filePath	File path. https://api.telegram.org/file/bot<token>/<filePath>.
   */
  case class File(fileId: String, fileSize: Option[Int], filePath: Option[String])

}
