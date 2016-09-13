package ru.finagram.tutorial

import com.twitter.util.Future
import ru.finagram.{ FinagramBot, Polling }
import ru.finagram.Answers._
import ru.finagram.api.Keyboard.{ oneTime, resize }
import ru.finagram.api._

object TutorialBot extends App with FinagramBot with AnswersExamples with Polling {
  override val token: String = from("/tutorial.token")

  on("/start", "/about", "/help") {
    markdown(
      """
        |*Finagram Tutorial Bot*
        |This Bot can show for you how use [Finagram project](https://github.com/finagram/finagram) for build Telegram Bots.
        |Just send /example command and follow the Bot instructions and examples.
        |""".stripMargin,
//      new InlineKeyboard()
//        .buttons("github" -> "https://github.com/finagram/finagram", "examples" -> "/example")
//        .createOpt()
      new Keyboard(resize, oneTime)
        .buttons("/example", "/about")
        .createOpt()
    )
  }

  on("/example") {
    markdown(
      """
        |*What are you interesting in:*
        |1. Hello world example: /hello
        |2. How receive message: /receive
        |3. How send answer: /answer
      """.stripMargin
    )
  }

  on("/hello") {
    markdown(code(from("/hello.scala")))
  }

  /**
   * Default handler for commands without handler.
   */
  override def defaultHandler(update: Update): Future[Option[Answer]] = text(s"Unsupported update $update")(update).map(Some.apply)

  run()

}
