package ru.finagram.api

import org.json4s.{ DefaultFormats, Extraction, FieldSerializer }
import org.json4s.JsonAST._
import org.json4s.native.Serialization
import ru.finagram.test.Spec

import scala.math.BigInt
import scala.util.Random

class AnswerSerializerSpec extends Spec {

  describe("serialize answer") {
    it("should create JObject with only expected fields") {
      // given:
      val id = BigInt(1)
      val answer = FlatAnswer(id.toLong, Random.nextString(12))

      // when:
      val json = AnswerSerializer.serialize(answer)

      // then:
      json match {
        case JObject(Seq(
          JField("chat_id", JInt(id)),
          JField("text", JString(answer.text))
        )) =>
        case _ => throw new Exception(s"Wrong json:\n$json\nfor answer:\n$answer")
      }
    }
  }

}
