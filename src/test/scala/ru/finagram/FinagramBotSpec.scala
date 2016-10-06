package ru.finagram

import com.twitter.util.{ Await, Future }
import ru.finagram.api._
import ru.finagram.test.Spec
import ru.finagram.Answers._

import scala.util.Random

class FinagramBotSpec extends Spec {

  val chat = Chat(12L, Random.nextString(12))

  trait TestBot extends FinagramBot with MessageReceiver {
    override val token: String = "123-123-123"

    override def run(): Unit = {}
  }

  describe("Bot with handler for text command") {
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
      answer should contain(FlatAnswer(chat.id, "it's work!"))
    }
  }
  describe("Bot with one handler for many text commands") {
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
      answer1 should contain(FlatAnswer(chat.id, "it's work!"))
      answer2 should contain(FlatAnswer(chat.id, "it's work!"))
    }
  }
  describe("Bot with handler for text command and TextMessage") {
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
      answer should contain(FlatAnswer(chat.id, "message handler"))
    }
  }
  describe("Bot without handlers for received update") {
    it("should invoke default handler") {
      // given:
      val bot = new AnyRef with TestBot {
        /**
         * Default handler for commands without handler.
         */
        override def defaultHandler(update: Update): Future[Option[Answer]] = {
          text("default answer")(update).map(Some.apply)
        }
      }

      // when:
      val answer = Await result bot.handle(MessageUpdate(1L, TextMessage(1L, None, 1L, chat, "/command")))

      // then:
      answer should contain("default answer")
    }
  }
}
