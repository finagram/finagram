package ru.finagram.tutorial

import ru.finagram.Answers._
import ru.finagram.api.Keyboard.{ oneTime, resize }
import ru.finagram.FinagramHandler
import ru.finagram.api.{ InlineKeyboard, Keyboard }

trait AnswersExamples extends FinagramHandler {
  on("/answer") {
    markdown(
      """
        |*Supported types of answer:*
        |1. Text /text
        |2. File /file
        |3. Sticker /sticker
      """.stripMargin
    )
  }

  on("/text") {
    markdown(
      """
        |*What kind of text answer you need:*
        |1. Flat text: /flat
        |2. Markdown: /markdown
        |3. Html: /html
      """.stripMargin
    )
  }

  val textAnswersKeyboard = new InlineKeyboard()
    .buttons("back" -> "/text", "other answers" -> "/answer", "all examples" -> "/examples")
    .createOpt()

  on("/flat") {
    markdown(
      s"""
        |*Flat answer example*
        |
        |${code(from("/text.scala"))}
      """.stripMargin,
      textAnswersKeyboard
    )
  }

  on("/markdown") {
    markdown(
      s"""
         |*Markdown answer example*
         |
         |${code(from("/markdown.scala"))}
      """.stripMargin,
      textAnswersKeyboard
    )
  }

  on("/html") {
    markdown(
      s"""
         |*Html answer example*
         |
         |${code(from("/html.scala"))}
      """.stripMargin,
      textAnswersKeyboard
    )
  }
}
