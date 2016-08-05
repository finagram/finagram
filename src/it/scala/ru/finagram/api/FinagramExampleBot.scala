package ru.finagram.api

import com.typesafe.config.ConfigFactory
import ru.finagram.{ FinagramBot, Polling }

object FinagramExampleBot extends App with FinagramBot with Polling with Answers {
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

  run()

  /**
   * Handle any errors.
   */
}
