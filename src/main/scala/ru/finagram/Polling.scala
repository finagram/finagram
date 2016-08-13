package ru.finagram

import java.util.concurrent.TimeUnit

import com.twitter.finagle.http.{ Message => _ }
import com.twitter.util._
import org.json4s.DefaultFormats
import org.slf4j.Logger
import ru.finagram.api._

/**
 * Implementation of the mechanism of long polling that invoked handlers for received messages.
 */
trait Polling extends MessageReceiver {
  val token: String
  val log: Logger

  /**
   * Timer for repeat [[poll]]
   */
  private val timer = new JavaTimer(true)
  /**
   * Default error handler
   */
  private val defaultErrorHandler: PartialFunction[Throwable, Unit] = {
    case e => log.warn("Not handled exception", e)
  }

  private[finagram] val client = TelegramClient()

  /**
   * Run process of get updates from Telegram.
   */
  override final def run(): Unit = {
    Await result repeat(poll, 0L)
  }

  def onError: PartialFunction[Throwable, Unit]

  /**
   * Invoked request, handle response with custom logic and send bot answer
   */
  private[finagram] final def poll(offset: Long): Future[Long] = {
    client.getUpdates(token, offset)
      .flatMap(handleUpdatesAndExtractNewOffset)
      .map(_.getOrElse(offset))
      .handle(
        // if something was wrong we should try handle message again from current offset
        onError.orElse(defaultErrorHandler).andThen(_ => offset)
      )
  }

  /**
   * Take and increment update id by one, and if update contains message,
   * this method invoke custom handler this message.
   *
   * @param updates sequence of the [[Update]] object from Telegram's response.
   * @return next offset.
   */
  private def handleUpdatesAndExtractNewOffset(updates: Seq[Update]): Future[Option[Long]] = {
    Future.collect(updates.map { update =>
      takeAnswerFor(update.message)
        .flatMap {
          case Some(answer) =>
            client.sendAnswer(token, answer)
          case None =>
            Future.Done
        }
        // increment offset
        .map(_ => update.updateId + 1)
    }).map(_.lastOption)
  }

  /**
   * Invoke handler for message and return answer from it. If handler will not found, then None
   * will returned.
   *
   * @param message message from Telegram's response.
   * @return custom bot answer to message or [[None]].
   */
  private def takeAnswerFor(message: Option[Message]): Future[Option[Answer]] = {
    message match {
      case Some(msg) =>
        Future(handle(msg) match {
          case Return(answer) =>
            Some(answer)
          case Throw(e) =>
            log.error(s"Exception on handle message $message", e)
            None
        })
      case None =>
        log.debug("Message is empty")
        Future.None
      case _ =>
        log.debug("Received not handled message: " + message)
        Future.None
    }
  }

  /**
   * If action returns successful future, repeat it action with result from future,
   * otherwise repeat action with initial argument.
   *
   * @param action some action, that return future as result.
   * @param init argument of the action.
   * @tparam T type of action result.
   * @return last invoked future.
   */
  private def repeat[T](action: (T) => Future[T], init: T): Future[T] = {
    action(init).delayed(Duration(1300, TimeUnit.MILLISECONDS))(timer).transform {
      case Return(result) =>
        repeat(action, result)
      case Throw(e) =>
        repeat(action, init)
    }
  }
}
