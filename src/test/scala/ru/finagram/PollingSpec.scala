package ru.finagram
import com.twitter.finagle.Service
import com.twitter.finagle.http.{ Request, Response }
import com.twitter.util.{ Await, Try }
import org.json4s.native.JsonMethods._
import org.json4s.{ DefaultFormats, Extraction, FieldSerializer }
import org.mockito.Mockito._
import org.slf4j.{ Logger, LoggerFactory }
import ru.finagram.api._
import ru.finagram.api.{ TelegramResponse$ => TelegramResponse }

class PollingSpec extends Spec with RandomObjects {

  private val log = LoggerFactory.getLogger(getClass)

  describe("poll") {
    it("should issue GET request to /bot<token>/getUpdates with offset") {
      // given:
      val token = randomString()
      val offset = randomInt()
      val answer = mock[Answer]
      val updates = randomUpdates(1)
      val http = clientWithResponse(responseWithContent(toJsonString(updates)))
      val polling = new TestPolling(token, http, (_) => answer)

      // when:
      Await result polling.poll(offset)

      // then:
      val captor = argumentCaptor[Request]
      verify(http).apply(captor.capture())
      val request = captor.getValue

      request.path should be(s"/bot$token/getUpdates")
      request.params("offset") should be(offset.toString)
    }
    it("should invoke handler as many times as updates in the response") {
      // given:
      val token = randomString()
      val answer = mock[Answer]
      val updates = randomUpdates(3)
      val http = clientWithResponse(responseWithContent(toJsonString(updates)))
      val polling = spy(new TestPolling(token, http, (_) => answer))

      // when:
      val nextOffset = Await result polling.poll(0)

      // then:
      verify(polling, times(3)).handle(any[Message])
      nextOffset should be(updates.result.last.updateId + 1)
    }
  }

  private class TestPolling(
    override val token: String,
    override val http: Service[Request, Response],
    answer: (Message) => Answer
  ) extends Polling {
    override val log: Logger = PollingSpec.this.log
    override def handle(message: Message): Try[Answer] = {
      log.info(s"$message")
      Try(answer(message))
    }
    override def onError: PartialFunction[Throwable, Unit] = { case e => throw e }
  }

  private def toJsonString(updates: Updates): String = {
    implicit val formats = DefaultFormats + FieldSerializer[TelegramResponse]()
    val str = compact(render(Extraction.decompose(updates).snakizeKeys))
    str
  }
}
