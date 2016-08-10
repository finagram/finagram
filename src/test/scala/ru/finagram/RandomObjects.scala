package ru.finagram

import ru.finagram.api.{ Chat, TextMessage, User }

import scala.util.Random

/**
 * Generate random instances of objects from Telegram api.
 */
trait RandomObjects {

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

  private def date = System.currentTimeMillis()
}
