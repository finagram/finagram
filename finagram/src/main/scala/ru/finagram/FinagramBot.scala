package ru.finagram

import com.twitter.finagle.http.{ Message => _ }
import org.slf4j.LoggerFactory
import ru.finagram.api.{ Answer, Message, _ }

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

  override private[finagram] val handlers = mutable.Map[String, (Update) => Answer]()

  /**
   * Create answer for message.
   *
   * @param update incoming update from Telegram.
   * @return answer if handler for message was found or [[None]].
   */
  override final def handle(update: Update): Option[Answer] = {
    update match {
      case MessageUpdate(_, message) =>
        message match {
          // invoke handler for text message
          case msg: TextMessage if (handlers.contains(extractCommand(msg))) =>
              val command = extractCommand(msg)
              log.debug(s"Invoke handler for command $command")
              Some(handlers(command)(update))
          // TODO add support of other message types
          case _ =>
            defaultHandler(update)
        }
      case _ =>
        ???
    }
  }

  /**
   * Default handler for commands without handler.
   */
   def defaultHandler(update: Update): Option[Answer] = None

  /**
   * Handle any errors.
   */
  def onError: PartialFunction[Throwable, Unit] = {
    case e => log.error("Something wrong", e)
  }

  /**
   * Return substring from text that contains only command.
   * Command should begin from '/' and end with space symbol or end of the string.
   *
   * @return substring that contains only command.
   */
  def extractCommand(message: TextMessage): String = {
    message.text.replaceFirst("\\s.*", "")
  }
}

