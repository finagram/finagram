package ru.finagram
import com.twitter.util.{ Await, Future }
import org.mockito.Mockito._
import org.slf4j.{ Logger, LoggerFactory }
import ru.finagram.api._
import ru.finagram.util.RandomObjects._
import ru.finagram.util.Spec

class PollingSpec extends Spec {

  private val log = LoggerFactory.getLogger(getClass)

  describe("poll") {
    it("should invoke handler as many times as updates in the response") {
      // given:
      val updates = randomUpdates(3)
      val client = mock[TelegramClient]
      doReturn(Future(updates.result)).when(client).getUpdates(any[String], any[Long], any[Option[Int]])
      doReturn(Future.Unit).when(client).sendAnswer(any[String], any[Answer])
      val polling = spy(new TestPolling(randomString(), client, (_) => mock[FlatAnswer]))

      // when:
      val nextOffset = Await result polling.poll(0)

      // then:
      verify(polling, times(3)).handle(any[Update])
      nextOffset should be(updates.result.last.updateId + 1)
    }
    it("should not stop polling when some handler throw exception") {
      // given:
      val updates = randomUpdates(3)
      val client = mock[TelegramClient]
      doReturn(Future(updates.result)).when(client).getUpdates(any[String], any[Long], any[Option[Int]])
      doReturn(Future.Unit).when(client).sendAnswer(any[String], any[Answer])
      val polling = spy(new TestPolling(randomString(), client, (_) => throw new Exception("On handle message example exception")))

      // when:
      Await result polling.poll(0)

      // then:
      verify(polling, times(3)).handle(any[Update])
    }
  }

  private class TestPolling(
    override val token: String,
    override val client: TelegramClient = mock[TelegramClient],
    answer: (Update) => Answer
  ) extends Polling {
    override val log: Logger = PollingSpec.this.log
    override def handle(update: Update): Option[Answer] = {
      log.info(s"$update")
      Some(answer(update))
    }
    // only IllegalArgumentException can be handled, but all other exception should not crash app
    override def handleError: PartialFunction[Throwable, Unit] = { case e: IllegalArgumentException => log.error(e.getMessage) }
  }
}
