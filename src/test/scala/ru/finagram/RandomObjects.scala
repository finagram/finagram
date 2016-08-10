package ru.finagram

import ru.finagram.api.{ Chat, TextMessage, User }

import scala.util.Random

/**
 * Generate random instances of objects from Telegram api.
 */
trait RandomObjects {

  def randomTextMessage(
    id: Int = Random.nextInt(),
    text: String = Random.nextString(12),
    user: Option[User] = randomUser(),
    chat: Chat = randomChat()
  ): TextMessage = TextMessage(id, user, date, chat, text)

  private def date = System.currentTimeMillis()
}
