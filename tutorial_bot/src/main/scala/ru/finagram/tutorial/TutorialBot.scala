package ru.finagram.tutorial

import ru.finagram.{ FinagramBot, Keyboard, Polling }
import ru.finagram.Answers._

object TutorialBot extends App with FinagramBot with Polling {
  override val token: String = from("/tutorial.token")

  on("/start") {
    val keyboard = new Keyboard()
      .oneTime()
      .resize()
      .buttons("/text")
      .createOpt()
    text("This is Finagram Tutorial Bot", keyboard)
  }

  val textAnswersKeyboard = new Keyboard()
    .oneTime()
    .resize()
    .buttons("/flat", "/markdown", "/html")
    .buttons("/start")
    .createOpt()

  on("/text") {
    text("What kind of text answer you need:", textAnswersKeyboard)
  }

  on("/flat") {
    markdown(code(from("/text.scala")), textAnswersKeyboard)
  }

  on("/markdown") {
    markdown(code(from("/markdown.scala")), textAnswersKeyboard)
  }

  on("/html") {
    markdown(code(from("/html.scala")), textAnswersKeyboard)
  }

  run()

  private def code(code: String) = {
    s"```java\n$code\n```"
  }
}
