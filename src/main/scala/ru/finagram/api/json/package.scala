package ru.finagram.api

import org.json4s.DefaultFormats

package object json {

  object Implicit {
    implicit val FORMATS = DefaultFormats + TelegramResponseSerializer + UpdateSerializer + MessageSerializer + AnswerSerializer
  }
}
