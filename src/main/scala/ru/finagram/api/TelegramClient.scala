package ru.finagram.api

import com.twitter.finagle.http.{ Method, Request, Response }
import com.twitter.finagle.{ Http, Service }
import com.twitter.util.{ Future, Return, Throw, Try }
import org.json4s.native.JsonMethods._
import org.json4s.{ DefaultFormats, Extraction }
import org.slf4j.LoggerFactory
import ru.finagram.UnexpectedResponseException

class TelegramClient private[finagram] (http: Service[Request, Response]) {

  private val log = LoggerFactory.getLogger(getClass)

  /**
   * Default formats for json.
   */
  private implicit val formats = DefaultFormats

  def getUpdates(token: String, offset: Long, limit: Option[Int] = None): Future[Seq[Update]] = {
    http(getUpdateRequest(token, offset, limit))
      .map(verifyResponseStatus)
      .map(extractUpdatesFromResponse)
  }

  /**
   * Create request to Telegram for get one update from offset.
   *
   * @param offset number of the last handled update.
   * @return http request
   */
  private def getUpdateRequest(token: String, offset: Long, limit: Option[Int] = None): Request = {
    val path = if(limit.isEmpty ) {
      s"/bot$token/getUpdates?offset=$offset"
    } else {
      s"/bot$token/getUpdates?offset=$offset&limit=${limit.get}"
    }
    Request(Method.Get, path)
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
   * Check https response. Correct response:
   * <ul>
   *   <li>contains status in diapason from 200 to 299</li>
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
   * Send bot answer as POST request.
   *
   * @param token
   * @param answer custom bot answer to message.
   */
  def sendAnswer(token: String, answer: Answer): Future[Unit] = {
    http(postAnswer(token, answer))
      .onSuccess(response => log.debug("Response to answer:\n" + response.contentString))
      .map(verifyResponseStatus)
      .map(_ => Unit)
  }

  /**
   * Create POST request to Telegram with custom bot answer rendered to json.
   *
   * @param token
   * @param answer custom bot answer to message.
   * @return http request.
   */
  private def postAnswer(token: String, answer: Answer): Request = {
    val content = compact(render(Extraction.decompose(answer).snakizeKeys))
    log.trace(s"Prepared answer $content")

    val request = answer match {
      case txt: TextAnswer =>
        Request(Method.Post, s"/bot$token/sendMessage")
      case sticker: StickerAnswer =>
        Request(Method.Post, s"/bot$token/sendSticker")
      case a => throw new NotImplementedError(s"Not implemented post answer for $a")
    }
    request.setContentTypeJson()
    request.contentString = content
    request
  }
}

object TelegramClient {

  def apply(): TelegramClient = {
    /**
     * Asynchronous http client.
     */
    val http = Http.client
      .withTls("api.telegram.org")
      .newService("api.telegram.org:443")
    new TelegramClient(http)
  }
}
