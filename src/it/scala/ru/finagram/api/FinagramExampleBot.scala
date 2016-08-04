package ru.finagram.api

import com.typesafe.config.ConfigFactory
import ru.finagram.FinagramBot

object FinagramExampleBot extends App with FinagramBot with AnswerFactory {
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
}
