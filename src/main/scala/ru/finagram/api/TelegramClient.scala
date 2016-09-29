package ru.finagram.api

import com.twitter.finagle.http.{ Method, Request, Response }
import com.twitter.finagle.{ Http, Service }
import com.twitter.util.{ Future, Return, Throw, Try }
import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods._
import org.slf4j.LoggerFactory
import ru.finagram.UnexpectedResponseException

/**
 * Contains methods for issue http requests to Telegram.
 */
class TelegramClient(http: Service[Request, Response] = Http.client
  .withTls("api.telegram.org")
  .newService("api.telegram.org:443")
) {

  private val log = LoggerFactory.getLogger(getClass)

  /**
   * Default formats for json.
   */
  private implicit val formats = DefaultFormats

  /**
   * Issue Http GET request to Telegram with offset and limit as query parameters.
   *
   * @param token
   * @param offset
   * @param limit
   * @return
   */
  def getUpdates(token: String, offset: Long, limit: Option[Int] = None): Future[Seq[Update]] = {
    http(createUpdateRequest(token, offset, limit))
      .map(verifyResponseStatus)
      .map(extractFromResponse[Updates])
      .map(_.result)
  }

  /**
   * Close the resource.
   * The returned Future is completed when the resource has been fully relinquished.
   */
  def close(): Future[Unit] = {
    http.close()
  }

  /**
   * Create request to Telegram for get updates from offset.
   *
   * @param offset number of the last handled update.
   * @return http request
   */
  private def createUpdateRequest(token: String, offset: Long, limit: Option[Int] = None): Request = {
    val path = if (limit.isEmpty) {
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
  private def extractFromResponse[T](response: Response): T = {
    val content = response.contentString
    log.trace(s"Received content: $content")
    Try(TelegramResponse(content)) match {
      case Return(result: T) =>
        result
      case Return(e: TelegramException) =>
        throw e
      case Throw(e) =>
        throw new UnexpectedResponseException("Parse response failed.", e)
    }
  }

  /**
   * Method to get basic info about a file and prepare it for downloading.
   *
   * @param token
   * @param fileId
   */
  def getFile(token: String, fileId: String): Future[File] = {
    http(createGetFileRequest(token, fileId))
      .map(verifyResponseStatus)
      .map(extractFromResponse[FileResponse])
      .map(_.result)
  }

  private def createGetFileRequest(token: String, fileId: String): Request = {
    val path = s"/bot$token/getFile?file_id=$fileId"
    Request(Method.Get, path)
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
      throw new UnexpectedResponseException(s"Unexpected response status: ${res.statusCode}.\n${res.contentString} ")
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
      .unit
  }

  /**
   * Create POST request to Telegram with custom bot answer rendered to json.
   *
   * @param token
   * @param answer custom bot answer to message.
   * @return http request.
   */
  private def postAnswer(token: String, answer: Answer): Request = {
    val content = compact(render(AnswerSerializer.serialize(answer)))
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