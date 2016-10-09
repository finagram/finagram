package ru.finagram.api

import org.json4s.JsonAST._
import org.scalatest.{ FunSpecLike, Matchers }
import ru.finagram.test.Utils

import scala.math.BigInt
import scala.util.Random

class AnswerSerializationSpec extends FunSpecLike with Matchers with Utils {

  describe("serialize answer") {
    it("should create JObject with only expected fields") {
      // given:
      val id = BigInt(1)
      val answer = FlatAnswer(id.toLong, Random.nextString(12))

      // when:
      val json = Answer.serialize(answer)

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
