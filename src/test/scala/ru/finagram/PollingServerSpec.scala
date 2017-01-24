package ru.finagram

import com.twitter.util.{ Await, Duration, Future }
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.{ FreeSpec, Matchers }
import org.slf4j.LoggerFactory
import ru.finagram.api._
import ru.finagram.test.{ MockitoSugar, Podam, TestException }

import scala.util.Random

class PollingServerSpec extends FreeSpec with Matchers with MockitoSugar with Podam {

  private val log = LoggerFactory.getLogger(getClass)

  val token = randomString()

  "Long polling" - {
    "should issue requests to Telegram at least two times for timeout x 3 period" in {
      // given:
      val polling = new PollingServer(token, _ => Future.Unit, Duration.fromMilliseconds(100)) {
        override val client = clientThatReturn(randomUpdatesWithMessages(1))
      }

      // when:
      polling.run()
      Thread.sleep(polling.requestTimeout.inMillis * 3)
      Await result polling.close()

      // then:
      verify(polling.client, Mockito.atLeast(2)).getUpdates(any[String], any[Long], any[Option[Int]])
    }
    "when receive response with update" - {
      "should invoke handler with it" in {
        // given:
        var isHandlerInvoked = false
        val polling = new PollingServer(token, _ => Future(isHandlerInvoked = true).unit) {
          override val client = clientThatReturn(randomUpdatesWithMessages(1))
        }

        // when:
        polling.run()
        Await result polling.close()

        // then:
        isHandlerInvoked should be(true)
      }
      "should return number of the last served offset" in {
        // given:
        val updates = randomUpdatesWithMessages(1)
        val polling = new PollingServer(token, _ => Future.Unit) {
          override val client = clientThatReturn(updates)
        }

        // when:
        val lastOffset = polling.run()
        polling.close()

        // then:
        Await result lastOffset should be(updates.result.last.updateId)
      }
      "should break polling if handler return future with exception" in {
        // given:
        val polling = new PollingServer(token, _ => Future.exception(TestException())) {
          override val client = clientThatReturn(randomUpdatesWithMessages(1))
        }
        // when:
        intercept[PollingException] {
          Await result polling.run()
        }
      }
    }
    "when receive response from Telegram with many updates " - {
      "should invoke handler as many times as many updates in the response" in {
        // given:
        val updatesCount = 3
        var handlerInvocationCount = 0
        val polling = new PollingServer(token, _ => Future(handlerInvocationCount += 1)) {
          override val client = clientThatReturn(randomUpdatesWithMessages(updatesCount))
        }

        // when:
        polling.run()
        Await result polling.close()

        // then:
        handlerInvocationCount should be(updatesCount)
      }
    }
  }

  private def randomString() = Random.alphanumeric.take(10).mkString

  private def randomUpdatesWithMessages(count: Int): Updates = {
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
}
