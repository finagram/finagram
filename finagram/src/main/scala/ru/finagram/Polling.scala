package ru.finagram

import java.util.concurrent.TimeUnit

import com.twitter.finagle.http.{ Message => _ }
import com.twitter.util._
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
    case e => log.error("Not handled exception:", e)
  }

  private[finagram] val client = new TelegramClient()

  /**
   * Run process of get updates from Telegram.
   */
  override final def run(): Unit = {
    Await result repeat(poll, 0L)
  }

  def handleError: PartialFunction[Throwable, Unit] = defaultErrorHandler

  /**
   * Invoked request, handle response with custom logic and send bot answer
   */
  private[finagram] final def poll(offset: Long): Future[Long] = {
    client.getUpdates(token, offset)
      .flatMap(handleUpdatesAndExtractNewOffset)
      .map(_.getOrElse(offset))
      .handle(
        // if something was wrong we should try handle message again from current offset
        handleError.orElse(defaultErrorHandler).andThen(_ => offset)
      )
  }

  /**
   * Take and increment update id by one, and if update contains message,
   * this method invoke custom handler for this message.
   *
   * @param updates sequence of the [[Update]] object from Telegram's response.
   * @return next offset.
   */
  private def handleUpdatesAndExtractNewOffset(updates: Seq[Update]): Future[Option[Long]] = {
    Future.collect {
      updates.map { update =>
        takeAnswerFor(update)
          .flatMap {
            case Some(answer) =>
              client.sendAnswer(token, answer)
            case None =>
              Future.Done
          }
          .map(_ => update.updateId + 1)
      }
    }.map(_.lastOption)
  }

  /**
   * Invoke handler for message and return answer from it. If handler will not found,
   * or exception will threw then return [[None]].
   * will returned.
   *
   * @param update incoming update from Telegram.
   * @return custom bot answer to message or [[None]].
   */
  private def takeAnswerFor(update: Update): Future[Option[Answer]] = {
    handle(update).handle {
       handleError.orElse(defaultErrorHandler).andThen(_ => None)
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
