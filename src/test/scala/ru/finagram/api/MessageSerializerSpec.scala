package ru.finagram.api

import java.util.UUID

import org.json4s.JsonAST.{ JObject, _ }
import org.json4s.native.JsonMethods._
import org.json4s.{ DefaultFormats, Extraction }
import org.scalatest.{ FunSpecLike, Matchers }
import org.scalatest.prop.TableDrivenPropertyChecks._
import ru.finagram.api.json.MessageSerializer
import ru.finagram.test.Utils
import uk.co.jemos.podam.api.{ AbstractRandomDataProviderStrategy, AttributeMetadata, PodamFactoryImpl }

import scala.util.Random

class MessageSerializerSpec extends FunSpecLike with Matchers with Utils {

  implicit val formats = DefaultFormats + MessageSerializer

  override val factory = new PodamFactoryImpl(CustomProviderStrategy)

  val randomTextMessage = random[TextMessage]
  val randomDocumentMessage = random[DocumentMessage]
  val randomLocationMessage = random[LocationMessage]
  val randomStickerMessage = random[StickerMessage]
  val randomVideoMessage = random[VideoMessage]
  val randomPhotoMessage = random[PhotoMessage]
  val randomVoiceMessage = random[VoiceMessage]

  describe("serialize message to json") {
    it(s"should create JObject for text message only with expected fields") {
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
      (randomDocumentMessage, "document", classOf[JObject]),
      (randomLocationMessage, "location", classOf[JObject]),
      (randomStickerMessage, "sticker", classOf[JObject]),
      (randomVideoMessage, "video", classOf[JObject]),
      (randomPhotoMessage, "photo", classOf[JArray]),
      (randomVoiceMessage, "voice", classOf[JObject])
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

  describe(s"deserialize message from json string") {

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
      it(s"should deserialize ${message.getClass}") {
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

  private object CustomProviderStrategy extends AbstractRandomDataProviderStrategy {
    override def getStringValue(attributeMetadata: AttributeMetadata): String = UUID.randomUUID().toString

    override def getMemoizedObject(attributeMetadata: AttributeMetadata): AnyRef = {
      // HACK: Class with field Option[Int] after compilation will contain field Option[Object]
      // and original type will be lost. This holds for every of primitive types.
      // Change Option[Object] to Option[Int] enough to success, but only for this test class.
      if (attributeMetadata.getAttrGenericArgs.contains(classOf[Object]))
        Some(Random.nextInt(100))
      else
        super.getMemoizedObject(attributeMetadata)
    }
  }

}
