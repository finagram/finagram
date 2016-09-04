package ru.finagram.api

import org.json4s.JsonAST._
import org.json4s.{ DefaultFormats, Extraction }
import ru.finagram.util.RandomObjects._
import ru.finagram.util.Spec

class MessageSerializerSpec extends Spec {

  implicit val formats = DefaultFormats + MessageSerializer

  describe("serialize to json") {
    it(s"should create expected JObject for text message with 'user'") {
      // given:
      val msg = randomTextMessage()

      // when:
      val json = Extraction.decompose(msg).snakizeKeys

      // then:
      json match {
        case JObject(Seq(
        JField("message_id", JInt(_)),
        JField("chat", JObject(_)),
        JField("date", JInt(_)),
        JField("from", JObject(_)),
        JField("text", JString(_))
        )) =>
        case _ => throw new Exception(s"Wrong json:\n$json")
      }
    }
    it(s"should create expected JObject for text message without 'user'") {
      // given:
      val msg = randomTextMessage(user = None)

      // when:
      val json = Extraction.decompose(msg).snakizeKeys

      // then:
      json match {
        case JObject(Seq(
        JField("message_id", JInt(_)),
        JField("chat", JObject(_)),
        JField("date", JInt(_)),
        JField("text", JString(_))
        )) =>
        case _ => throw new Exception(s"Wrong json:\n$json")
      }
    }
    it(s"should create expected JObject for sticker message") {
      // given:
      val msg = randomStickerMessage()

      // when:
      val json = Extraction.decompose(msg).snakizeKeys

      // then:
      json match {
        case JObject(Seq(
        JField("message_id", JInt(_)),
        JField("chat", JObject(_)),
        JField("date", JInt(_)),
        JField("from", JObject(_)),
        JField("sticker", JObject(_))
        )) =>
        case _ => throw new Exception(s"Wrong json:\n$json")
      }
    }
  }
}
