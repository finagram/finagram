package ru.finagram

import com.twitter.util.Await
import org.mockito.Mockito._
import ru.finagram.Answers._
import ru.finagram.api._
import ru.finagram.test.Spec

import scala.util.Random

class FinagramBotSpec extends Spec {

  val chat = Chat(12L, Random.nextString(12))

  describe("Bot without handlers for received update") {
    it("should invoke default handler") {
      // given:
      val bot = spy(new TestBot)
      val update = MessageUpdate(1L, TextMessage(1L, None, 1L, chat, "/command"))

      // when:
      val answer = Await result bot.handle(update)

      // then:
      answer should be(None)
      verify(bot).defaultHandler(update)
    }
  }
  describe("Bot with handler for text command") {
    it("should invoke registered command handler for update with message") {
      // given:
      val bot = new TestBot {
        on("/command") {
          text("it's work!")
        }
      }
      // when:
      val answer = Await result bot.handle(MessageUpdate(1L, TextMessage(1L, None, 1L, chat, "/command")))

      // then:
      answer should contain(FlatAnswer(chat.id, "it's work!"))
    }
    it("should invoke registered command handler for callback query") {
      // given:
      val bot = new TestBot {
        on("/command") {
          text("it's work!")
        }
      }
      val query = CallbackQuery(
        id = Random.nextString(10),
        from = random[User],
        message = None,
        data = "/command"
      )

      // when:
      val answer = Await result bot.handle(CallbackQueryUpdate(1L, query))

      // then:
      answer should contain(FlatAnswer(query.from.id, "it's work!"))
    }
    it("should throw exception if command patter is empty") {
      intercept[IllegalArgumentException] {
        new TestBot {
          on("") {
            text("Command pattern can't be empty")
          }
        }
      }
    }
    it("should throw exception if command patter is blank") {
      intercept[IllegalArgumentException] {
        new TestBot {
          on("    ") {
            text("Command pattern can't be blank")
          }
        }
      }
    }
  }
  describe("Bot with one handler for many text commands") {
    it("should invoke handler for every command") {
      // given:
      val bot = new TestBot {
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
  describe("Bot with handlers for text command and TextMessage") {
    it("should invoke handler for TextMessage and ignore handler for command") {
      // given:
      val bot = new TestBot {
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
}

class TestBot extends FinagramBot with MessageReceiver {
  override val token: String = "123-123-123"

  override def run(): Unit = {}
}
