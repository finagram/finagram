package ru.finagram.api

import org.json4s.{ DefaultFormats, Extraction, FieldSerializer, Formats, JObject, JValue, Serializer, TypeInfo }

object AnswerSerializer {

  implicit val formats = DefaultFormats + FieldSerializer[Answer](FieldSerializer.ignore("content"))

  def serialize(answer: Answer): JValue = {
    Extraction.decompose(answer).snakizeKeys
  }
}
