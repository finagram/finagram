package ru.finagram.test.matchers

import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.write
import org.json4s.{ DefaultFormats, Diff, Formats, JValue }
import org.scalatest.matchers._
import org.scalatest.{ FreeSpec, Matchers }

import scala.language.experimental.macros
import scala.language.higherKinds

object Json {

  def apply(right: String)(implicit formats: Formats): BeMatcher[String] =
    new BeMatcher[String] {
      def apply(left: String): MatchResult = {
        val jleft = parse(left)
        val jright = parse(right)
        MatchResult(
          jleft == jright,
          rawFailureMessage(jleft, jright),
          s"$left\nwas equals to\n$jright"
        )
      }

      override def toString: String = "json (" + right + ")"
    }

  private def rawFailureMessage(left: JValue, right: JValue)(implicit formats: Formats): String = {
    val Diff(changed, added, deleted) = right diff left
    val result = new StringBuilder(s"Not expected json:\n")
    if (changed.toOption.isDefined)
      result ++= s"Field changed:\n${write(changed)}\n"
    if (deleted.toOption.isDefined)
      result ++= s"Absent part:\n${write(deleted)}\n"
    if (added.toOption.isDefined)
      result ++= s"Not expected part:\n${write(added)}\n"
    result.toString
  }
}


class JsonSpec extends FreeSpec with Matchers {

  implicit val formats = DefaultFormats

  val json =
    """
      |{
      |  "value": 1,
      |  "array": [1,2,3],
      |  "field": "Hello"
      |}
    """.stripMargin

  "Json matcher" - {
    "should not throws exception when string contains expected json" in {
      // given:
      val actual = json
      // when:
      actual should be(Json(json))
    }
    "should print absent part of expected json" in {
      // given:
      val actual =
        """
          |{
          |  "value": 1,
          |  "field": "Hello"
          |}
        """.stripMargin
      // when:
      try {
        actual should be(Json(json))
      } catch {
        case e: Throwable =>
          e.getMessage should be(
            """|Not expected json:
               |Absent part:
               |{"array":[1,2,3]}
               |""".stripMargin)
      }
    }
    "should print added part of json" in {
      // given:
      val actual =
        """
          |{
          |  "value": 1,
          |  "array": [1,2,3],
          |  "field": "Hello",
          |  "new": "Not expected part"
          |}
        """.stripMargin
      // when:
      try {
        actual should be(Json(json))
      } catch {
        case e: Throwable =>
          e.getMessage should be(
            """|Not expected json:
               |Not expected part:
               |{"new":"Not expected part"}
               |""".stripMargin)
      }
    }
    "should print different part of json" in {
      // given:
      val actual =
        """
          |{
          |  "value": 2,
          |  "array": [1,2,3],
          |  "field": "Hello",
          |}
        """.stripMargin
      // when:
      try {
        actual should be(Json(json))
      } catch {
        case e: Throwable =>
          e.getMessage should be(
            """|Not expected json:
               |Field changed:
               |{"value":2}
               |""".stripMargin)
      }
    }
  }
}