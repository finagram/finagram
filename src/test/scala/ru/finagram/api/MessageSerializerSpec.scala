package ru.finagram.api

import org.json4s.JsonAST.{ JObject, _ }
import org.json4s.native.JsonMethods._
import org.json4s.{ DefaultFormats, Extraction }
import org.scalatest.prop.TableDrivenPropertyChecks._
import ru.finagram.test.Spec

class MessageSerializerSpec extends Spec {

  implicit val formats = DefaultFormats + MessageSerializer

  describe("serialize message to json") {
    it(s"should create JObject for text message only with expected fields") {
      // given:
      val msg = random[TextMessage]

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
      val msg = random[TextMessage].copy(from = None)

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
    val messages = Table[Message, String, Class[_ <: JValue]](
      ("Message", "Expected custom field", ""),
      (random[DocumentMessage], "document", classOf[JObject]),
      (random[LocationMessage], "location", classOf[JObject]),
      (random[StickerMessage], "sticker", classOf[JObject]),
      (random[VideoMessage], "video", classOf[JObject]),
      (random[PhotoMessage], "photo", classOf[JArray]),
      (random[VoiceMessage], "voice", classOf[JObject])
    )
    forAll(messages) { (msg, field, clazz) =>
      it(s"should create expected JObject with field $field") {
        // when:
        val json = Extraction.decompose(msg).snakizeKeys

        // then:
        json match {
          case JObject(Seq(
          JField("message_id", JInt(_)),
          JField("chat", JObject(_)),
          JField("date", JInt(_)),
          JField("from", JObject(_)),
          JField(`field`, c)
          )) =>
            c.getClass should be(clazz)

          case _ =>
            throw new Exception(s"Wrong json:\n${pretty(render(json))}")
        }
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
