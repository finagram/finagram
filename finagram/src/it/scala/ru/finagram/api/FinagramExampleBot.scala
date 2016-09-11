package ru.finagram.api

import com.typesafe.config.ConfigFactory
import ru.finagram.{ FinagramBot, Keyboard, NotHandledMessageException, Polling }
import ru.finagram.Answers._

import scala.util.Random

object FinagramExampleBot extends App with FinagramBot with Polling {
  override val token: String = ConfigFactory.load("example.conf").getString("token")

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



  run()

  /**
   * Default handler for commands without handler.
   */
  override def defaultHandler(msg: Message) = Some(text(s"Unknown message $msg")(msg))

  /**
   * Handle any errors.
   */
  override def handleError: PartialFunction[Throwable, Unit] = {
    case NotHandledMessageException(msg) =>
      log.warn(msg)
  }
}
