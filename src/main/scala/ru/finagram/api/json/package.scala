package ru.finagram.api

import org.json4s.{ DefaultFormats, Extraction }
import org.json4s.native.JsonMethods._

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

  def write(obj: Any): String = {
    compact(render(Extraction.decompose(obj).snakizeKeys))
  }
}
