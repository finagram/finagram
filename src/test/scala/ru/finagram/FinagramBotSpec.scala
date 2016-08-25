package ru.finagram

import ru.finagram.api.{ Answers, Chat, FlatAnswer, TextMessage }
import ru.finagram.util.Spec

import scala.util.Random

class FinagramBotSpec extends Spec {

  trait TestBot extends FinagramBot with MessageReceiver with Answers {
    override val token: String = "123-123-123"
    override def run(): Unit = ???
  }

  describe("handle message") {
    it("should invoke registered handler") {
      // given:
      val chat = Chat(12L, Random.nextString(12))
      val bot = new AnyRef() with TestBot {
        on("/command") {
          text("it's work!")
        }
      }
      // when:
      val answer = bot.handle(TextMessage(1L, None, 1L, chat, "/command")).get()

      // then:
      answer should be(FlatAnswer(chat.id, "it's work!"))
    }
    it("should invoke registered handler without arguments") {
      // given:
      val chat = Chat(12L, Random.nextString(12))
      val bot = new AnyRef() with TestBot {
        on("/command") {
          text("it's work!")
        }
      }
      Seq(" ", "\t", "\n").foreach {space =>
        // when:
        val answer = bot.handle(TextMessage(1L, None, 1L, chat, s"/command${space}some another text")).get()

        // then:
        answer should be(FlatAnswer(chat.id, "it's work!"))
      }
    }
  }
}
