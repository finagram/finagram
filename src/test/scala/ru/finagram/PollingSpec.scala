package ru.finagram

import com.twitter.util.{ Await, Future }
import org.mockito.Mockito._
import org.scalatest.{ FunSpecLike, Matchers }
import org.slf4j.LoggerFactory
import ru.finagram.api._
import ru.finagram.test.{ TestException, Utils }

import scala.util.Random

class PollingSpec extends FunSpecLike with Matchers with Utils {

  private val log = LoggerFactory.getLogger(getClass)

  describe("poll") {
    it("should invoke handler as many times as updates in the response") {
      // given:
      val updates = randomUpdatesWithMessage(3)
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
      val updates = randomUpdatesWithMessage(3)
      val client = mock[TelegramClient]
      doReturn(Future(updates.result)).when(client).getUpdates(any[String], any[Long], any[Option[Int]])
      doReturn(Future.Unit).when(client).sendAnswer(any[String], any[Answer])
      val polling = spy(new TestPolling(randomString(), client, (_) => throw new TestException("On handle message example exception")))

      // when:
      Await result polling.poll(0)

      // then:
      verify(polling, times(3)).handle(any[Update])
    }
    it("should send answer for message to Telegram") {
      // given:
      val token: String = randomString()
      val answer = mock[FlatAnswer]
      val updates = randomUpdatesWithMessage(1)
      val client = mock[TelegramClient]
      doReturn(Future(updates.result)).when(client).getUpdates(any[String], any[Long], any[Option[Int]])
      doReturn(Future.Unit).when(client).sendAnswer(any[String], any[Answer])
      val polling = new TestPolling(token, client, (_) => answer)

      // when:
      Await result polling.poll(0)

      // then:
      verify(client).sendAnswer(token, answer)
    }
  }

  private def randomString() = Random.nextString(10)

  private def randomUpdatesWithMessage(count: Int): Updates = {
    val k = Random.nextInt(100)
    Updates((1 to count).map(i => random[MessageUpdate].copy(updateId = i * k)))
  }

  private class TestPolling(
    override val token: String,
    override val client: TelegramClient = mock[TelegramClient],
    answer: (Update) => Answer
  ) extends Polling {
    override def handle(update: Update): Future[Option[Answer]] = {
      log.info(s"$update")
      Future {
        Some(answer(update))
      }
    }
    // only IllegalArgumentException can be handled, but all other exception should not crash app
    override def onError: PartialFunction[Throwable, Unit] = { case e: IllegalArgumentException => log.error(e.getMessage) }
  }
}
