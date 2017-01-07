package ru.finagram.test.matchers

import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.write
import org.json4s.{ Diff, Formats, JValue }
import org.scalatest.matchers._

import scala.language.experimental.macros
import scala.language.higherKinds

object Json {

  def apply(right: String)(implicit formats: Formats): BeMatcher[JValue] =
    new BeMatcher[JValue] {
      def apply(left: JValue): MatchResult = {
        val jright = parse(right)
        MatchResult(
          left == jright,
          rawFailureMessage(left, jright),
          s"$left\nwas equals to\n$jright"
        )
      }

      override def toString: String = "json (" + right + ")"
    }

  private def rawFailureMessage(left: JValue, right: JValue)(implicit formats: Formats): String = {
    val Diff(changed, added, deleted) = left diff right
    val result = new StringBuilder(s"Expected json is difference:\n")
    if (changed.toOption.isDefined)
      result ++= s"Changed:\n${write(changed)}\n"
    if (deleted.toOption.isDefined)
      result ++= s"Deleted:\n${write(deleted)}\n"
    if (added.toOption.isDefined)
      result ++= s"Added:\n${write(added)}\n"
    result.toString
  }
}
