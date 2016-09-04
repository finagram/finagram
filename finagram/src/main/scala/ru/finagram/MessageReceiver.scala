package ru.finagram

import ru.finagram.api.{ Answer, Message }

/**
 * Interface for strategy of receive messages from Telegram.
 */
private[finagram] trait MessageReceiver extends Runnable {

  /**
   * Create answer for message.
   *
   * @param message Message from Telegram.
   * @return answer or [[None]].
   * @throws Exception any type when unsuccessful handle message.
   */
  def handle(message: Message): Option[Answer]
}
