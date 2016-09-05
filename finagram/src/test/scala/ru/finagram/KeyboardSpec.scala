package ru.finagram

import ru.finagram.Keyboard.{ oneTime, resize, selective }
import ru.finagram.util.Spec

class KeyboardSpec extends Spec {

  describe("use parameters for build keyboard with specified properties") {
    it("should build keyboard without parameters") {
      // when:
      val keyboard = new Keyboard().create()
      // then:
      keyboard.selective should be(None)
      keyboard.oneTimeKeyboard should be(None)
      keyboard.resizeKeyboard should be(None)
    }

    it("should build selective keyboard") {
      // when:
      val keyboard = new Keyboard(selective).create()
      // then:
      keyboard.selective should contain (true)
      keyboard.oneTimeKeyboard should be(None)
      keyboard.resizeKeyboard should be(None)
    }

    it("should build selective, oneTime and resizable keyboard") {
      // when:
      val keyboard = new Keyboard(selective, oneTime, resize).create()
      // then:
      keyboard.selective should contain (true)
      keyboard.oneTimeKeyboard should contain (true)
      keyboard.resizeKeyboard should contain (true)
    }
  }
}
