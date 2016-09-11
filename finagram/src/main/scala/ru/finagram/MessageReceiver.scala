package ru.finagram

import ru.finagram.api.{ Answer, Update }

/**
 * Interface for strategy of receive messages from Telegram.
 */
private[finagram] trait MessageReceiver extends Runnable {

  /**
   * Create answer for message.
   *
   * @param update incoming update from Telegram.
   * @return answer or [[None]].
   * @throws Exception any type when unsuccessful handle message.
   */
  def handle(update: Update): Option[Answer]
}
