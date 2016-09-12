package ru.finagram.tutorial

import ru.finagram.Answers._
import ru.finagram.api.Keyboard.{ oneTime, resize }
import ru.finagram.FinagramHandler
import ru.finagram.api.{ InlineKeyboard, Keyboard }

trait AnswersExamples extends FinagramHandler {
  on("/answer") {
    text(
      """
        |Supported types of answer:
        |1. Text /text
        |2. File /file
        |3. Sticker /sticker
      """.stripMargin
    )
  }
}

trait TextAnswerExamples extends FinagramHandler {

  val textAnswersKeyboard = new InlineKeyboard()
    .buttons("flat text" -> "/flat", "markdown" -> "/markdown", "html" ->"/html")
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
}
