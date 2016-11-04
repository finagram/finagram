package ru.finagram

import com.twitter.util.Await
import org.scalatest.{ FreeSpec, Matchers }
import ru.finagram.Answers._
import ru.finagram.api.{ Chat, MessageUpdate, TextMessage, Updates }
import ru.finagram.test.Utils

import scala.util.Random

class AnswersSpec extends FreeSpec with Matchers with Utils {

  "build text answer" - {
    "from function" - {
      "should invoke function on inner thread pool" in {
        // given:
        val timeout = 300L
        val begin = System.currentTimeMillis()

        // when:
        val answer = text {
          Thread.sleep(timeout)
          ("done", None)
        }(randomMessageUpdate)

        // then:
        (System.currentTimeMillis() - begin) should be < timeout
        // and:
        (Await result answer).text should be("done")
        (System.currentTimeMillis() - begin) should be >= timeout
      }
    }
  }

  private def randomMessageUpdate = MessageUpdate(
    Random.nextLong(),
    TextMessage(
      messageId =  Random.nextLong(),
      date = System.currentTimeMillis(),
      chat = random[Chat],
      text = Random.nextString(5)
    )
  )
}