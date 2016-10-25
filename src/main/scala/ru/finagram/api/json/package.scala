package ru.finagram.api

import org.json4s.DefaultFormats

package object json {

  object Implicit {
    implicit val formats = DefaultFormats + TelegramResponseSerializer + UpdateSerializer + MessageSerializer + AnswerSerializer
  }
}
