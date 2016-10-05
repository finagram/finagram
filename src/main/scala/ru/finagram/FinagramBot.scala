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

  override private[finagram] val messageHandlers = mutable.Set[PartialFunction[MessageUpdate, Future[Answer]]]()

  override private[finagram] val commandHandlers = mutable.Map[String, (Update) => Future[Answer]]()

  private lazy val messageHandler = messageHandlers
    .foldLeft(PartialFunction.empty[MessageUpdate, Future[Answer]])((a, b) => a.orElse(b))

  /**
   * Create answer for message.
   *
   * @param update incoming update from Telegram.
   * @return answer if handler for message was found or [[None]].
   */
  override final def handle(update: Update): Future[Option[Answer]] = update match {
    case u: MessageUpdate if messageHandler.isDefinedAt(u) =>
      log.debug(s"Invoke handler for message ${u.message}")
      messageHandler.apply(u).map(Some.apply)

    case UpdateWithCommand(u) if commandHandlers.contains(u.command) =>
      log.debug(s"Invoke handler for command ${u.command}")
      commandHandlers(u.command)(update).map(Some.apply)

    case _ =>
      defaultHandler(update)
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
}




