package ru.finagram.api

import org.json4s.Extraction
import org.json4s.JsonAST._
import org.scalatest.{ FreeSpec, Matchers }
import ru.finagram.api.json.Implicit.formats
import ru.finagram.test.Utils

import scala.math.BigInt
import scala.util.Random

class AnswerSerializationSpec extends FreeSpec with Matchers with Utils {

  "serialize answer to json" - {
    "should create JObject with only expected fields" in {
      // given:
      val id = BigInt(1)
      val answer = FlatAnswer(id.toLong, Random.nextString(12))

      // when:
      val json = Extraction.decompose(answer).snakizeKeys

      // then:
      json match {
        case JObject(Seq(
          JField("chat_id", JInt(`id`)),
          JField("text", JString(answer.text))
        )) =>
        case _ => throw new Exception(s"Wrong json:\n$json\nfor answer:\n$answer")
      }
    }
  }

}
