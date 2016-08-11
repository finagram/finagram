package ru.finagram

import java.util.concurrent.TimeUnit

import com.twitter.finagle.Http
import com.twitter.finagle.http.{ Method, Request, Response, Status, Message => _ }
import com.twitter.util._
import org.json4s.native.JsonMethods._
import org.json4s.{ DefaultFormats, Extraction }
import org.slf4j.Logger
import ru.finagram.api.{ Answer, Message, StickerAnswer, TelegramException, TextAnswer, Update, Updates, Response => TelegramResponse }

/**
 * Implementation of the mechanism of long polling that invoked handlers for received messages.
 */
trait Polling extends MessageReceiver {
  val token: String
  val log: Logger
  /**
   * Asynchronous http client.
   */
  private[finagram] val http = Http.client
    .withTls("api.telegram.org")
    .newService("api.telegram.org:443")
  /**
   * Timer for repeat [[poll]]
   */
  private val timer = new JavaTimer(true)
  /**
   * Default formats for json.
   */
  private implicit val formats = DefaultFormats
  /**
   * Default error handler
   */
  private val defaultErrorHandler: PartialFunction[Throwable, Unit] = {
    case e => log.warn("Not handled exception", e)
  }

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
    http(getUpdateRequest(offset)).map(verifyResponseStatus)
      .map(extractUpdatesFromResponse)
      .flatMap(handleUpdatesAndExtractNewOffset)
      .handle(
        // if something was wrong we should try handle message again from current offset
        onError.orElse(defaultErrorHandler).andThen(_ => offset)
      )
  }

  /**
   * Create request to Telegram for get one update from offset.
   *
   * @param offset number of the last handled update.
   * @return http request
   */
  private def getUpdateRequest(offset: Long): Request = {
    Request(Method.Get, s"/bot$token/getUpdates?offset=$offset&limit=10")
  }

  /**
   * Extract sequence of [[Update]] objects from response content.
   *
   * @param response response from Telegram after request updates.
   * @return sequence of [[Update]] objects or None if updates is not exists.
   * @throws TelegramException when response is not ok (field 'ok' is false).
   * @throws UnexpectedResponseException when parsing of the response was failed.
   */
  private def extractUpdatesFromResponse(response: Response): Seq[Update] = {
    val content = response.contentString
    log.trace(s"Received content: $content")
    Try(TelegramResponse(content)) match {
      case Return(Updates(result)) =>
        log.debug(s"Received ${result.size} updates")
        result
      case Return(e: TelegramException) =>
        throw e
      case Throw(e) =>
        throw new UnexpectedResponseException("Parse response failed.", e)
    }
  }

  /**
   * Take and increment update id by one, and if update contains message,
   * this method invoke custom handler this message.
   *
   * @param updates sequence of the [[Update]] object from Telegram's response.
   * @return next offset.
   */
  private def handleUpdatesAndExtractNewOffset(updates: Seq[Update]): Future[Long] = {
    log.debug("Updates count: " + updates.size)
    Future.collect(updates.map { update =>
      takeAnswerFor(update.message)
        .flatMap(sendAnswer)
        .map(verifyResponseStatus)
        // increment offset
        .map(_ => update.updateId + 1)
    }).map(_.last)
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
   * Send bot answer as POST request if answer is defined otherwise just return 200 response.
   *
   * @param opt custom bot answer to message or [[None]].
   * @return http response.
   */
  private def sendAnswer(opt: Option[Answer]): Future[Response] = opt match {
    case Some(answer) =>
      http(postAnswer(answer))
        .onSuccess(response => log.debug("Response to answer:\n" + response.contentString))
    case None =>
      Future(Response(Status.Ok))
  }

  /**
   * Create POST request to Telegram with custom bot answer rendered to json.
   *
   * @param answer custom bot answer to message.
   * @return http request.
   */
  private def postAnswer(answer: Answer): Request = {
    val content = compact(render(Extraction.decompose(answer).snakizeKeys))
    log.trace(s"Prepared answer $content")

    val request = answer match {
      case txt: TextAnswer =>
        Request(Method.Post, s"/bot$token/sendMessage")
      case sticker: StickerAnswer =>
        Request(Method.Post, s"/bot$token/sendSticker")
      case _ => ???
    }
    request.setContentTypeJson()
    request.contentString = content
    request
  }

  /**
   * Check https response. Correct response:
   * <ul>
   *   <li>contains status in diapason from 200 to 299</li>
   *   <li>contains json content with field "ok" = true</li>
   * </ul>
   * @param res http response from Telegram.
   * @return received response.
   * @throws UnexpectedResponseException if response contains wrong status.
   */
  private def verifyResponseStatus(res: Response): Response = {
    if (!(200 to 299).contains(res.statusCode)) {
      throw new UnexpectedResponseException("Unexpected response status " + res.status)
    }
    res
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
