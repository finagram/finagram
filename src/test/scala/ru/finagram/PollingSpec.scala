package ru.finagram
import com.twitter.util.{ Await, Future, Try }
import org.mockito.Mockito._
import org.slf4j.{ Logger, LoggerFactory }
import ru.finagram.api._
import ru.finagram.util.{ RandomObjects, Spec }

class PollingSpec extends Spec with RandomObjects {

  private val log = LoggerFactory.getLogger(getClass)

  describe("poll") {
    it("should invoke handler as many times as updates in the response") {
      // given:
      val token = randomString()
      val answer = mock[FlatAnswer]
      val updates = randomUpdates(3)
      val client = mock[TelegramClient]
      doReturn(Future(updates.result)).when(client).getUpdates(any[String], any[Long], any[Option[Int]])
      doReturn(Future.Unit).when(client).sendAnswer(any[String], any[Answer])
      val polling = spy(new TestPolling(token, client, (_) => answer))

      // when:
      val nextOffset = Await result polling.poll(0)

      // then:
      verify(polling, times(3)).handle(any[Message])
      nextOffset should be(updates.result.last.updateId + 1)
    }
  }

  private class TestPolling(
    override val token: String,
    override val client: TelegramClient = mock[TelegramClient],
    answer: (Message) => Answer
  ) extends Polling {
    override val log: Logger = PollingSpec.this.log
    override def handle(message: Message): Try[Answer] = {
      log.info(s"$message")
      Try(answer(message))
    }
    override def onError: PartialFunction[Throwable, Unit] = { case e => throw e }
  }
}
