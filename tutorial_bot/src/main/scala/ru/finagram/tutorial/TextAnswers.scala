package ru.finagram.tutorial

import ru.finagram.Answers._
import ru.finagram.Keyboard.{ oneTime, resize }
import ru.finagram.{ FinagramHandler, Keyboard }

trait TextAnswers extends FinagramHandler {

  val textAnswersKeyboard = new Keyboard(oneTime, resize)
    .buttons("/start")
    .buttons("/flat", "/markdown", "/html")
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

  private def code(code: String) = {
    s"```java\n$code\n```"
  }

}
