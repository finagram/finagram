package ru.finagram.api.json

import org.json4s.JsonAST.{ JObject, _ }
import org.json4s.native.JsonMethods._
import org.json4s.{ DefaultFormats, Extraction }
import org.scalacheck.Arbitrary
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.{ FreeSpec, Matchers }
import ru.finagram.api._
import ru.finagram.test.{ MockitoSugar, RandomDataGenerator }

class MessageSerializerSpec extends FreeSpec with Matchers with MockitoSugar with RandomDataGenerator {

  implicit val formats = DefaultFormats + MessageSerializer

  implicit val arbUser: Arbitrary[Option[User]] = arbitrary(Some(random[User]))

  private val randomTextMessage = random[TextMessage]
  private val randomDocumentMessage = random[DocumentMessage]
  private val randomLocationMessage = random[LocationMessage]
  private val randomStickerMessage = random[StickerMessage]
  private val randomVideoMessage = random[VideoMessage]
  private val randomPhotoMessage = random[PhotoMessage]
  private val randomVoiceMessage = random[VoiceMessage]

  "serialize message to json" - {
    s"should create JObject for text message only with expected fields" in {
      // given:
      val msg = randomTextMessage

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
    s"should create expected JObject for text message without 'user'" in {
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
      (randomDocumentMessage, "document", classOf[JObject]),
      (randomLocationMessage, "location", classOf[JObject]),
      (randomStickerMessage, "sticker", classOf[JObject]),
      (randomVideoMessage, "video", classOf[JObject]),
      (randomPhotoMessage, "photo", classOf[JArray]),
      (randomVoiceMessage, "voice", classOf[JObject])
    )
    forAll(messages) { (msg, field, clazz) =>
      s"should create expected JObject with field $field" in {
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
            throw new Exception(s"Wrong json:\n$json\nExpected:" +
                                s"""
                                  |JObject(Seq(
                                  |          JField("message_id", JInt(_)),
                                  |          JField("chat", JObject(_)),
                                  |          JField("date", JInt(_)),
                                  |          JField("from", JObject(_)),
                                  |          JField($field, $clazz)
                                  |          ))
                                """.stripMargin)
        }
      }
    }
  }

  s"deserialize message from json string" - {

    val messages = Table[Message, String, String](
      ("Message", "Content field name", "Content field value"),
      (randomTextMessage, "text", s""""${randomTextMessage.text}""""),
      (randomDocumentMessage, "document", write(randomDocumentMessage.document)),
      (randomLocationMessage, "location", write(randomLocationMessage.location)),
      (randomPhotoMessage, "photo", write(randomPhotoMessage.photo)),
      (randomStickerMessage, "sticker", write(randomStickerMessage.sticker)),
      (randomVideoMessage, "video", write(randomVideoMessage.video)),
      (randomVoiceMessage, "voice", write(randomVoiceMessage.voice))
    )

    forAll(messages) { (message, fieldName, fieldValue) =>
      s"should deserialize ${message.getClass}" in {
        // given:
        val str =
          s"""
             |{
             |   "message_id":${message.messageId},
             |   "from":${write(message.from)},
             |   "chat":${write(message.chat)},
             |   "date":${message.date},
             |   "$fieldName":$fieldValue
             |}
        """.stripMargin

        // when:
        val actualMessage = parse(str).camelizeKeys.extract[Message]

        // then:
        actualMessage should be(message)
      }
    }
  }

  private def write(obj: AnyRef): String = compact(render(Extraction.decompose(obj).snakizeKeys))
}
