package ru.finagram.tutorial

import ru.finagram.{ FinagramBot, Keyboard, Polling }
import ru.finagram.Answers._
import ru.finagram.Keyboard.{ oneTime, resize }

object TutorialBot extends App with FinagramBot with TextAnswerExamples with Polling {
  override val token: String = from("/tutorial.token")

  on("/start") {
    markdown(
      """
        |*Finagram Tutorial Bot*
        |This Bot can show for you how use [Finagram project](https://github.com/finagram/finagram) for build Telegram Bots.
        |Just send /example command and follow the Bot instructions and examples.
        |""".stripMargin
    )
  }

  on("/example") {
    markdown(
      """
        |What are you interesting in:
        |1. Hello world example: /hello
        |2. How receive message: /receive
        |3. How send answer: /answer
      """.stripMargin)
  }

  on("/hello") {
    markdown(code(from("/hello.scala")))
  }

  run()
}
