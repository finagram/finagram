package ru.finagram

import com.twitter.util.{ Await, Duration, Future }
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.{ FreeSpec, Matchers }
import org.slf4j.LoggerFactory
import ru.finagram.api._
import ru.finagram.test.{ TestException, Utils }

import scala.util.Random

class PollingSpec extends FreeSpec with Matchers with Utils {

  private val log = LoggerFactory.getLogger(getClass)

  "Long polling" - {
    "should issue requests to Telegram at least two times for timeout x 3 period" in {
      // given:
      val bot = new TestPolling(clientThatReturn(randomUpdatesWithMessage(1)), Some(FlatAnswer(1L, "ping"))) {
        override val timeout = Duration.fromMilliseconds(100L)
      }

      // when:
      bot.run()
      Thread.sleep(bot.timeout.inMillis * 3)
      Await result bot.close()

      // then:
      verify(bot.client, Mockito.atLeast(2)).getUpdates(any[String], any[Long], any[Option[Int]])
    }
    "when receive response with update" - {
      "should send answer from handler to the Telegram" in {
        // given:
        val answer = mock[FlatAnswer]
        val updates = randomUpdatesWithMessage(1)
        val client = clientThatReturn(updates)
        val polling = new TestPolling(client, Some(answer))

        // when:
        Await result polling.poll(0)

        // then:
        verify(client).sendAnswer(polling.token, answer)
      }
      "should do nothing if handler returns None" in {
        // given:
        val updates = randomUpdatesWithMessage(1)
        val client = clientThatReturn(updates)
        val polling = new TestPolling(client, None)

        // when:
        Await result polling.poll(0)

        // then:
        verify(client, never()).sendAnswer(any[String], any[Answer])
      }
    }
    "when receive response from Telegram with many updates " - {
      "should invoke handler as many times as many updates in the response" in {
        // given:
        val updates = randomUpdatesWithMessage(3)
        val client = clientThatReturn(updates)
        val polling = spy(new TestPolling(client, Some(mock[FlatAnswer])))

        // when:
        val nextOffset = Await result polling.poll(0)

        // then:
        verify(polling, times(3)).handle(any[Update])
        nextOffset should be(updates.result.last.updateId + 1)
      }
    }
    "when receive response with update that can not be correctly handled" - {
      "should skip it update and increment offset" in {
        val updates = randomUpdatesWithMessage(3)
        val client = clientThatReturn(updates)
        val answers = Seq[() => Option[Answer]](
          () => Some(mock[FlatAnswer]),
          () => throw TestException(),
          () => Some(mock[FlatAnswer])
        ).iterator
        val polling = new TestPolling(client, answers.next.apply)

        // when:
        val offset = Await result polling.poll(0)

        // then:
        offset should be(updates.result.last.updateId + 1)
      }
    }
  }

  private def randomString() = Random.nextString(10)

  private def randomUpdatesWithMessage(count: Int): Updates = {
    val k = Random.nextInt(100)
    Updates((1 to count).map(i => random[MessageUpdate].copy(updateId = i * k)))
  }

  private def clientThatReturn(updates: Updates) = {
    val client = mock[TelegramClient]
    doReturn(Future(updates.result)).when(client).getUpdates(any[String], any[Long], any[Option[Int]])
    doReturn(Future.Unit).when(client).sendAnswer(any[String], any[Answer])
    doReturn(Future.Unit).when(client).close()
    client
  }

  private class TestPolling(
    override val client: TelegramClient = mock[TelegramClient],
    answer: => Option[Answer]
  ) extends Polling {
    override val token: String = randomString()

    override def handle(update: Update): Future[Option[Answer]] = {
      log.info(s"$update")
      Future {
        answer
      }
    }
  }
}
