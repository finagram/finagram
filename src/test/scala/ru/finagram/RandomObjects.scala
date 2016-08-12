package ru.finagram

import ru.finagram.api._

import scala.util.Random
import scala.math.abs

/**
 * Generate random instances of objects from Telegram api.
 */
trait RandomObjects {

  def randomUpdates(count: Int) = {
    val k = abs(randomInt())
    Updates((1 to count).map(i => randomUpdate(i * k)))
  }

  def randomUpdate(id: Int = randomInt()) = Update(
    id,
    Some(randomTextMessage())
  )

  def randomUser() = User(
    id = randomInt(),
    firstName = randomString(),
    lastName = Some(randomString()),
    username = Some(randomString())
  )

  def randomString() = Random.nextString(12)

  def randomInt() = Random.nextInt()

  def randomChat() = Chat(
    id = randomInt(),
    `type` = randomString(),
    title = Some(randomString()),
    firstName = Some(randomString()),
    lastName = Some(randomString()),
    username = Some(randomString())
  )

  def randomTextMessage(
    id: Int = Random.nextInt(),
    text: String = Random.nextString(12),
    user: Option[User] = Some(randomUser()),
    chat: Chat = randomChat()
  ): TextMessage = TextMessage(id, user, date, chat, text)

  def randomStickerMessage(

  )

  private def date = System.currentTimeMillis()
}
