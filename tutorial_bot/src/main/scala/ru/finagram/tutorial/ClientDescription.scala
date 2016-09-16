package ru.finagram.tutorial

import ru.finagram.Answers._
import ru.finagram.FinagramHandler

trait ClientDescription extends FinagramHandler {

  on("/client") {
    markdown(
      Content.not_supported
    )
  }
}
