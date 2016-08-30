package ru.finagram

import com.twitter.finagle.http.{ Message => _ }
import com.twitter.util.{ Throw, Try }
import org.slf4j.LoggerFactory
import ru.finagram.api._

import scala.collection.mutable

/**
 * Trait for implementation of the bot logic.
 */
trait FinagramBot {

  this: MessageReceiver =>

  // Logic for handle messages from user
  val log = LoggerFactory.getLogger(getClass)

  private val handlers = mutable.Map[String, (Message) => Answer]()

  /**
   * Token of the bot.
   */
  val token: String

  /**
   * Handle any errors.
   */
  def onError: PartialFunction[Throwable, Unit] = {
    case e => log.error("Something wrong", e)
  }

  /**
   * Add handle for specified text from user.
   * Every text should contain only one handle otherwise [[IllegalArgumentException]] will be thrown.
   *
   * @param text Text from user. Cannot be empty.
   * @param handler Logic for create answer for received text.
   */
  final def on(text: String)(handler: (Message) => Answer): Unit = {
    if (text.trim.isEmpty) {
      throw new IllegalArgumentException("Text cannot be empty")
    }
    if (handlers.contains(text)) {
      throw new IllegalArgumentException(s"Handler for command $text already registered.")
    }
    handlers(text) = handler
  }

  /**
   * Create answer for message.
   *
   * @param message Message from Telegram.
   * @return answer.
   */
  // FIXME user should have way to skip message
  override final def handle(message: Message): Try[Answer] = {
    message match {
      // invoke handler for text message
      case msg: TextMessage if handlers.contains(msg.command) =>
        log.debug(s"Invoke handler for message $message")
        Try(handlers(msg.command)(message))
      // TODO add support of other message types
      case _ =>
        Throw(new NotHandledMessageException("Received not handled message: " + message))
    }
  }
}

