package ru.finagram

import com.twitter.util.{ Await, Future }
import org.mockito.Mockito._
import org.scalatest.{ FreeSpec, Matchers }
import ru.finagram.Answers._
import ru.finagram.api._
import ru.finagram.test.{ TestException, Utils }

import scala.util.Random

class FinagramBotSpec extends FreeSpec with Matchers with Utils {

  val chat = Chat(12L, Random.nextString(12))

  "Bot" - {
    "should throw exception if command patter is empty" in {
      intercept[IllegalArgumentException] {
        new TestBot {
          on("") {
            text("Command pattern can't be empty")
          }
        }
      }
    }
    "should throw exception if command patter is blank" in {
      intercept[IllegalArgumentException] {
        new TestBot {
          on("    ") {
            text("Command pattern can't be blank")
          }
        }
      }
    }
  }
  "Bot without handlers for received update" - {
    val bot = spy(new TestBot)
    "when handle any updates" - {
      "should invoke default handler" in {
        val update = MessageUpdate(1L, TextMessage(1L, None, 1L, chat, "/command"))
        val answer = Await result bot.handle(update)

        answer should be(None)
        verify(bot).defaultHandler(update)
      }
    }
  }
  "Bot with handler for text command" - {
    val bot = new TestBot {
      on("/command") {
        text("it's work!")
      }
    }
    "when handle update with text message" - {
      "should invoke registered command handler" in {
        // given:
        val update: MessageUpdate = MessageUpdate(1L, TextMessage(1L, None, 1L, chat, "/command"))

        // when:
        val answer = Await result bot.handle(update)

        // then:
        answer should contain(FlatAnswer(chat.id, "it's work!"))
      }
    }
    "when handle callback query" - {
      "should invoke registered command handler" in {
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
    }
  }
  "Bot with one handler for many text commands" - {
    val bot = new TestBot {
      on("/command_1", "/command_2") {
        text("it's work!")
      }
    }
    "should invoke handler for every command" in {
      // given:
      val update1: MessageUpdate = MessageUpdate(1L, TextMessage(1L, None, 1L, chat, "/command_1"))
      val update2: MessageUpdate = MessageUpdate(1L, TextMessage(1L, None, 1L, chat, "/command_2"))

      // when:
      val answer1 = Await result bot.handle(update1)
      val answer2 = Await result bot.handle(update2)

      // then:
      answer1 should contain(FlatAnswer(chat.id, "it's work!"))
      answer2 should contain(FlatAnswer(chat.id, "it's work!"))
    }
  }
  "Bot with handlers for text command and TextMessage" - {
    val bot = new TestBot {
      on("/command") {
        text("command handler")
      }
      on[TextMessage] {
        text("message handler")
      }
    }
    "should invoke handler for TextMessage and ignore handler for command" in {
      // given:
      val update: MessageUpdate = MessageUpdate(1L, TextMessage(1L, None, 1L, chat, "/command"))

      // when:
      val answer = Await result bot.handle(update)

      // then:
      answer should contain(FlatAnswer(chat.id, "message handler"))
    }
  }
  "Bot with exceptions handler" - {
    val bot = new TestBot {
      on("/command") {
        case _: Update => Future.exception[Answer](new TestException())
      }

      onError {
        case _: TestException => throw TestException("Escalated")
      }
    }
    "should escalate exception" in {
      intercept[TestException] {
        // when:
        Await result bot.handle(MessageUpdate(1L, TextMessage(1L, None, 1L, chat, "/command")))
      }
    }
    "should exception" in {
      // given:
      val bot = new TestBot {
        on("/command") {
          case _: Update => Future.exception[Answer](new TestException())
        }

        onError {
          case _: TestException => None
        }
      }
      // when:
      val answer = Await result bot.handle(MessageUpdate(1L, TextMessage(1L, None, 1L, chat, "/command")))

      // then:
      answer should be(None)
    }
  }

}

class TestBot extends FinagramBot with MessageReceiver {
  override val token: String = "123-123-123"

  override def run(): Unit = {}
}
