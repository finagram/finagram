package ru.finagram

import com.twitter.finagle.Service
import com.twitter.finagle.http.{ Request, Response, Statuses }
import com.twitter.util.{ Await, Future }
import org.json4s.native.JsonMethods._
import org.json4s.{ DefaultFormats, Extraction }
import org.mockito.Mockito._
import org.scalatest.{ FreeSpec, Matchers }
import ru.finagram.api.json.TelegramResponseSerializer
import ru.finagram.api._
import ru.finagram.test.{ MockitoSugar, Podam }
import ru.finagram.test.matchers.Json

import scala.util.Random

class TelegramClientSpec extends FreeSpec with Matchers with MockitoSugar with Podam {

  implicit val formats = DefaultFormats

  "Telegram client" - {
    "when get updates" - {
      "should issue GET to /bot<token>/getUpdates with offset and limit" in {
        // given:
        val token = randomToken
        val offset = Random.nextInt(100)
        val limit = Random.nextInt(5)
        val http = clientWithResponse(responseWithContent(toJsonString(randomUpdatesWithMessage(0))))
        val client = new TelegramClient(http)

        // when:
        Await result client.getUpdates(token, offset, Some(limit))

        // then:
        val captor = argumentCaptor[Request]
        verify(http).apply(captor.capture())
        val request = captor.getValue

        request.path should be(s"/bot$token/getUpdates")
        request.params("offset") should be(offset.toString)
        request.params("limit") should be(limit.toString)
      }
      "should return sequence of updates from Telegram" in {
        // given:
        val updates = randomUpdatesWithMessage(3)
        val http = clientWithResponse(responseWithContent(toJsonString(updates)))
        val client = new TelegramClient(http)

        // when:
        val result = Await result client.getUpdates(randomToken, 1L)

        // then:
        result should contain allElementsOf updates.result
      }
      s"should throw ${classOf[UnexpectedResponseException]} when Telegram return response with status != 200" in {
        // given:
        val http = clientWithResponse(Response(Statuses.BAD_REQUEST))
        val client = new TelegramClient(http)

        intercept[UnexpectedResponseException] {
          // when:
          Await result client.getUpdates(randomToken, 1L)
        }
      }
      s"should throw ${classOf[TelegramException]} when Telegram return response with 'ok' = false" in {
        // given:
        val exception = TelegramException("Example", Some(-1))
        val http = clientWithResponse(responseWithContent(toJsonString(exception)))
        val client = new TelegramClient(http)

        intercept[TelegramException] {
          // when:
          Await result client.getUpdates(randomToken, 1L)
        }
      }
      s"should throw ${classOf[UnexpectedResponseException]} when Telegram return wrong json" in {
        // given:
        val http = clientWithResponse(responseWithContent("illegal json"))
        val client = new TelegramClient(http)

        intercept[UnexpectedResponseException] {
          // when:
          Await result client.getUpdates(randomToken, 1L)
        }
      }
    }
    "when send answer with text message" - {
      "should issue POST to /bot<token>/sendMessage" in {
        // given:
        val answer = randomFlatAnswer
        val http = clientWithResponse(responseWithContent(""))
        val client = new TelegramClient(http)
        // when:
        client.sendAnswer(randomToken, answer)
        // then:
        val request = argumentCaptor[Request]
        verify(http).apply(request.capture())
        request.getValue.contentString should be(Json(
          s"""
             |{
             |  "chat_id":${answer.chatId},
             |  "text":"${answer.text}"
             |}
        """.stripMargin))
      }
    }
    "when send answer with sticker" - {
      "should issue POST to /bot<token>/sendSticker" in {
        // given:
        val answer = randomSticker
        val http = clientWithResponse(responseWithContent(""))
        val client = new TelegramClient(http)
        // when:
        client.sendAnswer(randomToken, answer)
        // then:
        val request = argumentCaptor[Request]
        verify(http).apply(request.capture())
        request.getValue.contentString should be(Json(
          s"""
             |{
             |  "chat_id":${answer.chatId},
             |  "sticker":"${answer.sticker}"
             |}
        """.stripMargin))
      }
    }
  }

  def clientWithResponse(response: Response): Service[Request, Response] = {
    val http = mock[Service[Request, Response]]
    doReturn(Future(response)).when(http).apply(any[Request])
    http
  }

  def responseWithContent(content: String): Response = {
    val response = Response()
    response.setContentTypeJson()
    response.contentString = content
    response
  }

  private def randomToken = randomAlphaNumStr

  private def toJsonString(response: TelegramResponse): String = {
    implicit val formats = DefaultFormats + TelegramResponseSerializer
    val str = compact(render(Extraction.decompose(response).snakizeKeys))
    str
  }

  private def randomUpdatesWithMessage(count: Int): Updates = {
    val k = Random.nextInt(100)
    Updates((1 to count).map(i => random[MessageUpdate].copy(updateId = i * k)))
  }

  private def randomFlatAnswer = FlatAnswer(
    chatId = Random.nextLong(),
    text = randomAlphaNumStr
  )

  private def randomSticker = StickerAnswer(
    chatId = Random.nextLong(),
    sticker = randomAlphaNumStr
  )

  private def randomAlphaNumStr = {
    Random.alphanumeric.take(12).mkString
  }
}
