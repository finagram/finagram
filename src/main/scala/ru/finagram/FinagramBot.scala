package ru.finagram

import com.twitter.util.Future
import org.json4s.native.JsonMethods._
import org.json4s.{ DefaultFormats, Extraction }
import org.slf4j.LoggerFactory
import ru.finagram.api.json.UpdateSerializer
import ru.finagram.api.{ Answer, _ }

import scala.collection.mutable

/**
 * Trait for implementation of the bot logic.
 */
trait FinagramBot extends FinagramHandler {

  this: MessageReceiver =>
  private val log = LoggerFactory.getLogger(getClass)

  override private[finagram] val messageHandlers = mutable.Set[PartialFunction[MessageUpdate, Future[Answer]]]()
  override private[finagram] val commandHandlers = mutable.Map[String, (Update) => Future[Answer]]()
  private lazy val messageHandler = messageHandlers
    .foldLeft(PartialFunction.empty[MessageUpdate, Future[Answer]])((a, b) => a.orElse(b))
  /**
   * Token of the bot.
   */
  val token: String

  private val defaultErrorHandler: PartialFunction[(Update, Throwable), Future[Option[Answer]]] = {
    case (u, e) =>
      implicit val formats = DefaultFormats + UpdateSerializer
      log.error(s"Exception on handle update:\n${pretty(render(Extraction.decompose(u).snakizeKeys))}", e)
      Future.None
  }

  private var errorHandler: PartialFunction[(Update, Throwable), Future[Option[Answer]]] = defaultErrorHandler

  /**
   * Create answer for message.
   *
   * @param update incoming update from Telegram.
   * @return answer if handler for message was found or [[None]].
   */
  override final def handle(update: Update): Future[Option[Answer]] = {
    val answer = update match {
      case u: MessageUpdate if messageHandler.isDefinedAt(u) =>
        log.debug(s"Invoke handler for message ${u.message}")
        messageHandler.apply(u).map(Some.apply)

      case UpdateWithCommand(u) if commandHandlers.contains(u.command) =>
        log.debug(s"Invoke handler for command ${u.command}")
        commandHandlers(u.command)(update).map(Some.apply)

      case _ =>
        defaultHandler(update)
    }
    answer.rescue {
      case e =>
        errorHandler.applyOrElse((update, e), defaultErrorHandler)
    }
  }

  /**
   * Default handler for commands without handler.
   */
  def defaultHandler(update: Update): Future[Option[Answer]] = Future.None

  /**
   * Set exception handler. If this method invoked more than one times, then last argument will used
   * as exceptions handler.
   *
   * @param handler function for recovery update after exception.
   */
  def onError(handler: PartialFunction[(Update, Throwable), Future[Option[Answer]]]) = {
    errorHandler = handler
  }
}




