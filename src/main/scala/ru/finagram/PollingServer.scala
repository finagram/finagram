package ru.finagram

import java.util.concurrent.TimeUnit

import com.twitter.util._
import org.slf4j.{ Logger, LoggerFactory }
import ru.finagram.api._

/**
 * Implementation of the mechanism of long polling that invoked handlers for received messages.
 *
 * @param token token for Telegram bot.
 * @param updateHandler handler for received updates. Invoked consistently after previous invocation was done.
 * @param requestTimeout timeout between requests.
 */
class PollingServer(
  val token: String,
  val updateHandler: (Update) => Future[Unit],
  val requestTimeout: Duration = Duration(700, TimeUnit.MILLISECONDS)
) extends Closable with CloseAwaitably {

  private val log: Logger = LoggerFactory.getLogger(getClass)

  /**
   * Timer for repeat [[poll]]
   */
  private val timer = new JavaTimer(true)

  private[finagram] val client = new TelegramClient()


  @volatile
  private var isStarted = false
  private var polling: Future[Long] = Future.value(0)

  /**
   * Stop process of get updates from Telegram.
   */
  override final def close(deadline: Time): Future[Unit] = synchronized {
    closeAwaitably {
      isStarted = false
      polling.unit
    }
  }

  /**
   * Run process of get updates from Telegram.
   *
   * @param offset offset from which to start polling
   * @return future with last served offset
   */
  final def run(offset: Long = 0): Future[Long] = synchronized {
    isStarted = true
    polling = repeat(poll, offset)
    polling.map(_ - 1)
  }

  /**
   * Invoked request, handle response with custom logic and send bot answer
   */
  private final def poll(offset: Long): Future[Long] = {
    client.getUpdates(token, offset)
      .flatMap(handleUpdatesAndExtractNewOffset)
      .map(_.getOrElse(offset))
  }

  /**
   * Take and increment update id by one, and invoke handler for every received updates.
   *
   * @param updates sequence of the [[Update]] object from Telegram's response.
   * @return next offset.
   */
  private def handleUpdatesAndExtractNewOffset(updates: Seq[Update]): Future[Option[Long]] = {
    val iterator = updates.iterator
    Future.whileDo(iterator.hasNext) {
      val update = iterator.next()
      updateHandler(update)
    }.map(_ => updates.lastOption.map(_.updateId + 1))
  }

  /**
   * If action returns successful future, repeat it action with result from future,
   * otherwise repeat action with initial argument.
   *
   * @param action some action, that return future as result.
   * @param init argument of the action.
   * @return last invoked future or future with init if action was failed.
   */
  private def repeat(action: (Long) => Future[Long], init: Long): Future[Long] = {
    if (isStarted) {
      action(init).delayed(requestTimeout)(timer).transform {
        case Return(result) =>
          repeat(action, result)
        case Throw(e) =>
          Future.exception(PollingException(init, e))
      }
    } else {
      Future.value(init)
    }
  }
}
