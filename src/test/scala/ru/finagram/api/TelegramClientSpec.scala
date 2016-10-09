package ru.finagram.api

import com.twitter.finagle.http.Request
import com.twitter.util.Await
import org.json4s.native.JsonMethods._
import org.json4s.{ DefaultFormats, Extraction, FieldSerializer }
import org.mockito.Mockito._
import org.scalatest.{ FunSpecLike, Matchers }
import ru.finagram.test.Utils

import scala.util.Random

class TelegramClientSpec extends FunSpecLike with Matchers with Utils {

  describe("get updates") {
    it("should issue GET to /bot<token>/getUpdates with offset and limit") {
      // given:
      val token = Random.nextString(10)
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
  }

  private def toJsonString(updates: Updates): String = {
    implicit val formats = DefaultFormats + FieldSerializer[TelegramResponse]()
    val str = compact(render(Extraction.decompose(updates).snakizeKeys))
    str
  }

  private def randomUpdatesWithMessage(count: Int): Updates = {
    val k = Random.nextInt(100)
    Updates((1 to count).map(i => random[MessageUpdate].copy(updateId = i * k)))
  }
}
