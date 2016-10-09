package ru.finagram.api

import org.scalatest.{ FunSpecLike, Matchers }
import ru.finagram.test.Utils

import scala.util.Random

class TextAnswerSpec extends FunSpecLike with Matchers with Utils {

  describe("text field in Answer") {
    it(s"should throw $ContentIsTooLongException when length of content will great than 4096") {
      intercept[ContentIsTooLongException] {
        // when:
        FlatAnswer(1L, Random.nextString(5000))
      }
    }
  }

}
