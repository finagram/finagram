package ru.finagram.tutorial

import ru.finagram.{ FinagramBot, Keyboard, Polling }
import ru.finagram.Answers._
import ru.finagram.Keyboard.{ oneTime, resize }

object TutorialBot extends App with FinagramBot with TextAnswers with Polling {
  override val token: String = from("/tutorial.token")

  on("/start") {
    val keyboard = new Keyboard(oneTime, resize)
      .buttons("/text")
      .createOpt()
    text("This is Finagram Tutorial Bot", keyboard)
  }

  run()
}
