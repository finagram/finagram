package ru.finagram

import java.util.concurrent.TimeUnit

import com.twitter.finagle.Http
import com.twitter.finagle.http.{ Method, Request, Response, Status, Message => _ }
import com.twitter.util._
import org.json4s.native.JsonMethods._
import org.json4s.{ DefaultFormats, Extraction, JObject }
import org.slf4j.Logger
import ru.finagram.api._

/**
 * Implementation of the mechanism of long polling that invoked handlers for received messages.
 */
trait Polling extends MessageReceiver {
  val token: String
  def onError: PartialFunction[Throwable, Unit]
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

  /**
   * Invoked request, handle response with custom logic and send bot answer
   */
  private[finagram] def poll(offset: Long): Future[Long] = {
    // TODO add support of some Updates
    // invoke request
    http(getUpdateRequest(offset)).map(verifyResponse)
      // extract update
      .map(extractUpdateFromResponse).flatMap {
      case Some(update) =>
        // invoke custom logic for handle update and take new offset
        handleUpdateAndExtractNewOffset(update)
      case None =>
        // just use current offset again
        Future(offset)
    }.handle(
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
   * Extract if exist [[Update]] object from response content.
   *
   * @param response response from Telegram after request updates.
   * @return [[Update]] or [[None]]
   */
  private def extractUpdateFromResponse(response: Response): Option[Update] = {
    val content = response.contentString
    log.debug(s"Received content: $content")
    Update(content)
  }

  /**
   * Take and increment update id by one, and if update contains message,
   * this method invoke custom handler this message.
   *
   * @param update [[Update]] object from Telegram's response.
   * @return next offset.
   */
  private def handleUpdateAndExtractNewOffset(update: Update): Future[Long] = {
    takeAnswerFor(update.message)
      .flatMap(sendAnswer)
      .map(verifyResponse)
      // increment offset
      .map(_ => update.updateId + 1)
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
   * @throws UnexpectedResponseException if response contains wrong status or illegal content.
   */
  private def verifyResponse(res: Response): Response = {
    if (!(200 to 299).contains(res.statusCode)) {
      throw new UnexpectedResponseException("Unexpected response status " + res.status)
    }
    val content = res.contentString
    log.trace(s"Verify response with content:\n$content")
    parse(content) match {
      case json: JObject =>
        if (json.values.contains("ok") && !(json \ "ok").extract[Boolean]) {
          throw new UnexpectedResponseException("Response is not OK")
        }
      case _ =>
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
