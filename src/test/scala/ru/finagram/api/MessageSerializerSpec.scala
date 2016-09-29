package ru.finagram.api

import org.json4s.JsonAST._
import org.json4s.{ DefaultFormats, Extraction }
import ru.finagram.test.RandomObjects._
import ru.finagram.test.Spec

import scala.util.Random

class MessageSerializerSpec extends Spec {

  implicit val formats = DefaultFormats + MessageSerializer

  describe("serialize message to json") {
    it(s"should create JObject for text message only with expected fields") {
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

  describe(s"deserialize ${classOf[Message]} from json string") {
    it(s"should deserialize ${classOf[DocumentMessage]}") {
      // given:

      // when:

      // then:
    }
  }
}
