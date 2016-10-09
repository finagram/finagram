package ru.finagram

import com.twitter.util.Future
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
   */
  def handle(update: Update): Future[Option[Answer]]
}
