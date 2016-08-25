package ru.finagram.util

import ru.finagram.api._

import scala.math.abs
import scala.util.Random

/**
 * Generate random instances of objects from Telegram api.
 */
trait RandomObjects {

  def randomString() = Random.nextString(12)

  def randomInt() = Random.nextInt()

  def randomUser() = User(
    id = randomInt(),
    firstName = randomString(),
    lastName = Some(randomString()),
    username = Some(randomString())
  )

  def randomChat() = Chat(
    id = randomInt(),
    `type` = randomString(),
    title = Some(randomString()),
    firstName = Some(randomString()),
    lastName = Some(randomString()),
    username = Some(randomString())
  )

  def randomSticker(
    fileId: String = randomString(),
    width: Int = randomInt(),
    height: Int = randomInt(),
    thumb: Option[PhotoSize] = None,
    emoji: Option[String] = None,
    fileSize: Option[Int] = None) = Sticker(fileId, width, height, thumb, emoji, fileSize)

  def randomTextMessage(
    id: Int = Random.nextInt(),
    text: String = Random.nextString(12),
    user: Option[User] = Some(randomUser()),
    chat: Chat = randomChat()
  ): TextMessage = TextMessage(id, user, date, chat, text)

  def randomStickerMessage(
    messageId: Long = randomInt(),
    from: Option[User] = Some(randomUser()),
    date: Long = date,
    chat: Chat = randomChat(),
    sticker: Sticker = randomSticker()
  ) = StickerMessage(messageId, from, date, chat, sticker)

  def randomUpdates(count: Int) = {
    val k = abs(randomInt())
    Updates((1 to count).map(i => randomUpdate(i * k)))
  }

  def randomUpdate(id: Int = randomInt()) = Update(
    id,
    Some(randomTextMessage())
  )

  private def date = System.currentTimeMillis()
}