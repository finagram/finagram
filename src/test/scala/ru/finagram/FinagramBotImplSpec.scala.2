package ru.finagram

import com.twitter.finagle.Service
import com.twitter.finagle.http.{ Methods, Request, Response }
import org.slf4j.LoggerFactory
import org.mockito.Mockito._
import ru.finagram.FinagramBot.Handler

class FinagramBotImplSpec extends Spec {

  private val log = LoggerFactory.getLogger(getClass)

  implicit val errorHandler: PartialFunction[Throwable, Unit] = { case e: Throwable => throw e }

  def fixture(handlers: (String, Handler)*)
    (implicit errorHandler: PartialFunction[Throwable, Unit]) = {
    val token = randomString()
    val http = mock[Service[Request, Response]]
    val bot = new Polling(
      token,
      http,
      handlers.toMap,
      errorHandler,
      log
    )
    (token, bot, http)
  }


  describe("get updates") {
    it("should invoke GET request to /bot<token>/getUpdates with specified offset") {
      // given:
      val (token, bot, http) = fixture()
      val offset = 4

      // when:
      bot.getUpdates(offset)

      // then:
      val request = {
        val captor = argumentCaptor[Request]
        verify(http).apply(captor.capture())
        captor.getValue
      }
      request.method should be(Methods.GET)
      request.path should be(s"/bot$token/getUpdates")
      request.params("offset") should be(offset.toString)
    }
  }
}
