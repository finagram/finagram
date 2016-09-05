package ru.finagram

import com.twitter.finagle.http.{ Message => _ }
import org.slf4j.LoggerFactory
import ru.finagram.api._

import scala.collection.mutable

/**
 * Trait for implementation of the bot logic.
 */
trait FinagramBot extends FinagramHandler {

  this: MessageReceiver =>

  // Logic for handle messages from user
  val log = LoggerFactory.getLogger(getClass)

  /**
   * Token of the bot.
   */
  val token: String

  override private[finagram] val handlers = mutable.Map[String, (Message) => Answer]()

  /**
   * Create answer for message.
   *
   * @param message Message from Telegram.
   * @return answer if handler for message was found or [[None]].
   */
  override final def handle(message: Message): Option[Answer] = {
    message match {
      // invoke handler for text message
      case msg: TextMessage =>
        if (handlers.contains(msg.command)) {
          log.debug(s"Invoke handler for message $message")
          Some(handlers(msg.command)(message))
        } else {
          None
        }
      // TODO add support of other message types
      case _ =>
        throw new NotHandledMessageException("Received not handled message: " + message)
    }
  }

  /**
   * Handle any errors.
   */
  def onError: PartialFunction[Throwable, Unit] = {
    case e => log.error("Something wrong", e)
  }
}

