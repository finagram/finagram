package ru.finagram.api

import com.typesafe.config.ConfigFactory
import ru.finagram.{ FinagramBot, NotHandledMessageException, Polling }
import ru.finagram.Answers._

import scala.util.Random

object FinagramExampleBot extends App with FinagramBot with Polling {
  override val token: String = from("/example.token")

  on("/text") {
    text("flat text")
  }

  on("/markdown") {
    markdown("*markdown text*")
  }

  on("/html") {
    html("<b>html text</b>")
  }

  on("/sticker") {
    sticker("BQADBAADtgQAAjZHEwABA70wjTd86fIC")
  }

  on("/one", "/two") {
    text("/one or /two")
  }

  on("/keyboard") {
    val keyboard = new Keyboard()
      .buttons("1", "2", "3")
      .buttons("4", "5", "6")
      .create()
    text("Keyboard", Some(keyboard))
  }

  on("/inlinekeyboard") {
    val keyboard = new InlineKeyboard()
      .buttons("1" -> "/1", "2" -> "3")
      .buttons("google" -> "https://google.com", "stackoverflow" -> "http://stackoverflow.com/")
      .create()
    text("Keyboard", Some(keyboard))
  }

  run()

  /**
   * Default handler for commands without handler.
   */
  override def defaultHandler(update: Update) = Some(text(s"Unknown update $update")(update))

  /**
   * Handle any errors.
   */
  override def handleError: PartialFunction[Throwable, Unit] = {
    case NotHandledMessageException(msg) =>
      log.warn(msg)
  }
}
