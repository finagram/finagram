package ru.finagram

import ru.finagram.api.Keyboard.{ oneTime, resize, selective }
import ru.finagram.api.{ Keyboard, KeyboardButton }
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

  describe("add string buttons to keyboard") {
    it("should build keyboard with one row of string buttons") {
      // when:
      val keyboard = new Keyboard().buttons("1", "2", "3").create()
      // then:
      keyboard.keyboard should have size(1)
      keyboard.keyboard.head should contain allOf(
        KeyboardButton("1"),
        KeyboardButton("2"),
        KeyboardButton("3"))
    }
    it("should build keyboard with two row of string buttons") {
      // when:
      val keyboard = new Keyboard()
        .buttons("1", "2", "3")
        .buttons("4")
        .create()
      // then:
      keyboard.keyboard should have size(2)
      keyboard.keyboard.head should contain allOf(
        KeyboardButton("1"),
        KeyboardButton("2"),
        KeyboardButton("3"))
      keyboard.keyboard.last should contain(KeyboardButton("4"))
    }
  }
}
