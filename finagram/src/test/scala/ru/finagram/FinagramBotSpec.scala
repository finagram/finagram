package ru.finagram

import com.twitter.util.Await
import ru.finagram.api._
import ru.finagram.util.Spec
import ru.finagram.Answers._

import scala.util.Random
import ru.finagram.util.RandomObjects._

class FinagramBotSpec extends Spec {

  trait TestBot extends FinagramBot with MessageReceiver {
    override val token: String = "123-123-123"
    override def run(): Unit = {}
  }

  describe("handle update") {
    it("should invoke registered handler") {
      // given:
      val chat = Chat(12L, Random.nextString(12))
      val bot = new AnyRef with TestBot {
        on("/command") {
          text("it's work!")
        }
      }
      // when:
      val answer = Await result bot.handle(MessageUpdate(1L, TextMessage(1L, None, 1L, chat, "/command")))

      // then:
      answer should contain (FlatAnswer(chat.id, "it's work!"))
    }
    it("should invoke registered handler without arguments") {
      // given:
      val chat = Chat(12L, Random.nextString(12))
      val bot = new AnyRef with TestBot {
        on("/command") {
          text("it's work!")
        }
      }
      Seq(" ", "\t", "\n").foreach {space =>
        // when:
        val answer = Await result bot.handle(MessageUpdate(1L, TextMessage(1L, None, 1L, chat, s"/command${space}some another text")))

        // then:
        answer should contain (FlatAnswer(chat.id, "it's work!"))
      }
    }
    it("should not throw exception if handler for message was not found") {
      // given:
      val bot = new AnyRef with TestBot
      // when:
      val result = Await result bot.handle(randomMessageUpdate())
      // then exception not expected:
      result should be(None)
    }
  }
}
