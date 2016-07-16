package ru.finagram.api

import com.typesafe.config.ConfigFactory
import ru.finagram.FinagramBot

object FinagramExampleBot extends App with FinagramBot {
  override val token: String = ConfigFactory.load("example.conf").getString("token")

  on("/text") {
    answer text "flat text"
  }

  on("/markdown") {
    answer markdown "*markdown text*"
  }

  on("/html") {
    answer html "<b>html text</b>"
  }

  on("/sticker") {
    answer sticker "BQADBAADtgQAAjZHEwABA70wjTd86fIC"
  }

  run()
}
