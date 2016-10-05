package ru.finagram

import com.twitter.util.Await
import ru.finagram.api._
import ru.finagram.test.Spec
import ru.finagram.Answers._

import scala.util.Random

class FinagramBotSpec extends Spec {

  trait TestBot extends FinagramBot with MessageReceiver {
    override val token: String = "123-123-123"
    override def run(): Unit = {}
  }

  val chat = Chat(12L, Random.nextString(12))

  describe("handle update") {
    it("should invoke registered command handler") {
      // given:
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
    it("should invoke handler for every command") {
      // given:
      val bot = new AnyRef with TestBot {
        on("/command_1", "/command_2") {
          text("it's work!")
        }
      }
      // when:
      val answer1 = Await result bot.handle(MessageUpdate(1L, TextMessage(1L, None, 1L, chat, "/command_1")))
      val answer2 = Await result bot.handle(MessageUpdate(1L, TextMessage(1L, None, 1L, chat, "/command_2")))

      // then:
      answer1 should contain (FlatAnswer(chat.id, "it's work!"))
      answer2 should contain (FlatAnswer(chat.id, "it's work!"))
    }
    it("should invoke handler for TextMessage and ignore handler for command") {
      // given:
      val bot = new AnyRef with TestBot {
        on("/command") {
          text("command handler")
        }
        on[TextMessage] {
          text("message handler")
        }
      }
      // when:
      val answer = Await result bot.handle(MessageUpdate(1L, TextMessage(1L, None, 1L, chat, "/command")))

      // then:
      answer should contain (FlatAnswer(chat.id, "message handler"))
    }
  }
}
