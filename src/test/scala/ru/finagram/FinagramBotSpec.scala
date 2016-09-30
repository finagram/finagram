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
  }
}
