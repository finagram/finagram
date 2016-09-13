package ru.finagram

import com.twitter.finagle.http.{ Message => _ }
import com.twitter.util.Future
import org.slf4j.LoggerFactory
import ru.finagram.api.{ Answer, _ }

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

  override private[finagram] val handlers = mutable.Map[String, (Update) => Future[Answer]]()

  /**
   * Create answer for message.
   *
   * @param update incoming update from Telegram.
   * @return answer if handler for message was found or [[None]].
   */
  override final def handle(update: Update): Future[Option[Answer]] = {
    extractCommand(update) match {
      case Some(command) if handlers.contains(command) =>
        log.debug(s"Invoke handler for command $command")
        handlers(command)(update).map(Some.apply)
      case _ =>
        defaultHandler(update)
    }
  }

  /**
   * Default handler for commands without handler.
   */
   def defaultHandler(update: Update): Future[Option[Answer]] = Future.None

  /**
   * Handle any errors.
   */
  def onError: PartialFunction[Throwable, Unit] = {
    case e => log.error("Something wrong", e)
  }

  private def extractCommand(update: Update): Option[String] = {
    // TODO maybe delegate it to the external 'Extractor'?
    update match {
      case MessageUpdate(_, message) =>
        message match {
          // invoke handler for text message
          case msg: TextMessage =>
            Some(msg.text.replaceFirst("\\s.*", ""))

          case _ => None
        }

      case CallbackQueryUpdate(_, callback) =>
        Some(callback.data.replaceFirst("\\s.*", ""))

      case _ => ???
    }

  }
}

