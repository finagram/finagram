package ru.finagram
import com.twitter.finagle.Service
import com.twitter.finagle.http.{ Request, Response }
import com.twitter.util.{ Await, Try }
import org.slf4j.{ Logger, LoggerFactory }
import ru.finagram.api.{ Answer, Message }
import org.mockito.Mockito._
import org.scalatest.Entry

import scala.util.Random

class PollingSpec extends Spec with RandomObjects {

  private val log = LoggerFactory.getLogger(getClass)

  describe("poll") {
    it("should issue GET request to /bot<token>/getUpdates with offset and limit") {
      // given:
      val token = Random.nextString(12)
      val offset = Random.nextInt()
      val answer = mock[Answer]
      val http = clientWithResponse(responseWithContent(randomTextMessage()))
      val poll = new TestPolling(token, http, (_) => answer)

      // when:
      Await result poll.poll(offset)

      // then:
      val captor = argumentCaptor[Request]
      verify(http).apply(captor.capture())
      val request = captor.getValue

      request.path should be(s"bot$token/getUpdates")
      request.getParams() should contain allOf(Entry("offset", offset), Entry("limit", 10))
    }
  }

  private class TestPolling(
    override val token: String,
    override val http: Service[Request, Response],
    answer: (Message) => Answer) extends Polling {
    override val log: Logger = PollingSpec.this.log
    override def handle(message: Message): Try[Answer] = Try(answer(message))
    override def onError: PartialFunction[Throwable, Unit] = { case _ => }
  }
}
