package ru.finagram

import java.util.concurrent.TimeUnit

import com.twitter.util._
import org.json4s.{ DefaultFormats, Extraction }
import org.json4s.native.JsonMethods._
import org.slf4j.{ Logger, LoggerFactory }
import ru.finagram.api._

/**
 * Implementation of the mechanism of long polling that invoked handlers for received messages.
 */
trait Polling extends MessageReceiver {

  protected val token: String

  private val log: Logger = LoggerFactory.getLogger(getClass)

  /**
   * Timer for repeat [[poll]]
   */
  private val timer = new JavaTimer(true)

  private[finagram] val client = new TelegramClient()

  /**
   * Timeout between requests.
   */
  val timeout = Duration(700, TimeUnit.MILLISECONDS)

  @volatile
  private var isStarted = false
  private val isStopped = Promise[Unit]

  /**
   * Run process of get updates from Telegram.
   */
  override final def run(): Unit = {
    isStarted = true
    Await result repeat(poll, 0L)
    isStopped.setDone()
  }

  /**
   * Stop process of get updates from Telegram.
   */
  override final def close(deadline: Time): Future[Unit] = closeAwaitably {
    isStarted = false
    client.close().join(isStopped).unit
  }

  /**
   * Invoked request, handle response with custom logic and send bot answer
   */
  private[finagram] final def poll(offset: Long): Future[Long] = {
    client.getUpdates(token, offset)
      .flatMap(handleUpdatesAndExtractNewOffset)
      .map(_.getOrElse(offset))
      .handle{
        case e =>
          log.error("Not handled exception:", e)
          offset
      }
  }

  /**
   * Take and increment update id by one, and if update contains message,
   * this method invoke custom handler for this message.
   *
   * @param updates sequence of the [[Update]] object from Telegram's response.
   * @return next offset.
   */
  private def handleUpdatesAndExtractNewOffset(updates: Seq[Update]): Future[Option[Long]] = {
    val iterator = updates.iterator
    Future.whileDo(iterator.hasNext) {
      val update = iterator.next()
      takeAnswerFor(update)
        .flatMap {
          case Some(answer) =>
            client.sendAnswer(token, answer)
          case None =>
            Future.Done
        }
    }.map(_ => updates.lastOption.map(_.updateId + 1))
  }

  /**
   * Invoke handler for message and return answer from it.
   * If exception will threw then return [[None]] will returned.
   *
   * @param update incoming update from Telegram.
   * @return custom bot answer to message or [[None]].
   */
  private def takeAnswerFor(update: Update): Future[Option[Answer]] = {
    handle(update).rescue {
      case e =>
        implicit val formats = DefaultFormats + UpdateSerializer
        log.error(s"Exception on handle update:\n${pretty(render(Extraction.decompose(update).snakizeKeys))}", e)
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
  private def repeat[T](action: (T) => Future[T], init: T): Future[Unit] = {
    if (isStarted) {
      action(init).delayed(timeout)(timer).transform {
        case Return(result) =>
          repeat(action, result)
        case Throw(e) =>
          repeat(action, init)
      }
    } else {
      Future.Unit
    }
  }
}
