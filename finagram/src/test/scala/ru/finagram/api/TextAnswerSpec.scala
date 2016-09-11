package ru.finagram.api

import org.json4s.Extraction
import ru.finagram.util.RandomObjects._
import ru.finagram.util.Spec

import scala.util.Random

class TextAnswerSpec extends Spec {

  describe("text field in Answer") {
    it(s"should throw $ContentIsTooLongException when length of content will great than 4096") {
      intercept[ContentIsTooLongException] {
        // when:
        FlatAnswer(1L, Random.nextString(5000))
      }
    }
  }

}
