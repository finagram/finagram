package ru.finagram

import com.twitter.util.Try
import ru.finagram.api.{ Answer, Message }

/**
 * Interface for strategy of receive messages from Telegram.
 */
trait MessageReceiver extends Runnable {

  /**
   * Create answer for message.
   *
   * @param message Message from Telegram.
   * @return answer or None.
   */
  def handle(message: Message): Try[Answer]
}
