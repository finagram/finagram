package ru.finagram

import java.util.concurrent.TimeUnit

import com.twitter.finagle.Service
import com.twitter.finagle.http.{ Method, Request, Response, Status, Message => _ }
import com.twitter.util._
import org.json4s.native.JsonMethods._
import org.json4s.{ DefaultFormats, Extraction, JObject }
import org.slf4j.Logger
import ru.finagram.FinagramBot.Handler
import ru.finagram.api._

/**
 * Implementation of the mechanism of long polling that invoked handlers for received messages.
 */
private[finagram] class FinagramBotImpl(
  val token: String,
  val http: Service[Request, Response],
  val handlers: Map[String, Handler],
  val errorHandler: PartialFunction[Throwable, Unit],
  val log: Logger
) {

  /**
   * Invoked request, handle response with custom logic and send bot answer
   */
  private[finagram] lazy val logic: (Long) => Future[Long] = (offset) => {
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
      errorHandler.orElse(defaultErrorHandler).andThen(_ => offset)
    )
  }

  /**
   * Timer for repeat [[logic]]
   */
  private[finagram] val timer = new JavaTimer(true)
  /**
   * Default formats for json.
   */
  private[finagram] implicit val formats = DefaultFormats
  /**
   * Default error handler
   */
  private[finagram] val defaultErrorHandler: PartialFunction[Throwable, Unit] = {
    case e => log.warn("Not handled exception", e)
  }

  /**
   * Run process of get updates from Telegram.
   *
   * @param init initial offset
   * @return fist not handled offset
   */
  def getUpdates(init: Long): Future[Long] = {
    repeat(logic, init)
  }

  /**
   * Create request to Telegram for get one update from offset.
   *
   * @param offset number of the last handled update.
   * @return http request
   */
  private[finagram] def getUpdateRequest(offset: Long): Request = {
    Request(Method.Get, s"/bot$token/getUpdates?offset=$offset&limit=1")
  }

  /**
   * Extract if exist [[Update]] object from response content.
   *
   * @param response response from Telegram after request updates.
   * @return [[Update]] or [[None]]
   */
  private[finagram] def extractUpdateFromResponse(response: Response): Option[Update] = {
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
  private[finagram] def handleUpdateAndExtractNewOffset(update: Update): Future[Long] = {
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
  private[finagram] def takeAnswerFor(message: Option[Message]): Future[Option[Answer]] = {
    message match {
      // invoke handler for text message
      case Some(TextMessage(_, _, _, _, text)) if handlers.contains(text) =>
        log.debug(s"Invoke handler for message ${message.get}")
        handlers(text)(message.get)
          .map(answer => Some(answer))
      case None =>
        log.debug("Message is empty")
        Future.None
      // TODO add support of other message types
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
  private[finagram] def sendAnswer(opt: Option[Answer]): Future[Response] = opt match {
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
  private[finagram] def postAnswer(answer: Answer): Request = {
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
  private[finagram] def verifyResponse(res: Response): Response = {
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
  private[finagram] def repeat[T](action: (T) => Future[T], init: T): Future[T] = {
    action(init).delayed(Duration(1300, TimeUnit.MILLISECONDS))(timer).transform {
      case Return(result) =>
        repeat(action, result)
      case Throw(e) =>
        repeat(action, init)
    }
  }
}
