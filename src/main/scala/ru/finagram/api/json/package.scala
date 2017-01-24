package ru.finagram.api

import org.json4s.{ DefaultFormats, Extraction, Formats }
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

  def compactWrite(obj: Any)(implicit formats: Formats): String = {
    compact(render(Extraction.decompose(obj).snakizeKeys))
  }
}
