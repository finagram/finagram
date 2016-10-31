package ru.finagram.api

import org.json4s.DefaultFormats

package object json {

  val serializers = Seq(
    TelegramResponseSerializer,
    UpdateSerializer,
    MessageSerializer,
    AnswerSerializer,
    KeyboardMarkupSerializer
  )

  object Implicit {
    implicit val formats = DefaultFormats ++ serializers
  }

}
