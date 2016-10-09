package ru.finagram.api

import java.net.URL
import org.scalatest.{ FunSpecLike, Matchers }

import ru.finagram.test.Utils

class InlineKeyboardSpec extends FunSpecLike with Matchers with Utils {

  describe("create inline keyboard") {
    it(s"should create ${classOf[InlineKeyboard]} with two rows of callback and url buttons") {
      // when:
      val keyboard: InlineKeyboardMarkup = new InlineKeyboard()
        .buttons("callback 1" -> "/callback1", "link 1" -> "https://google.com")
        .buttons("link 2" -> new URL("http://example.com"), "callback 2" -> "answer for callback 2")
        .create()

      // then:
      keyboard.inlineKeyboard should have size (2)
      keyboard.inlineKeyboard.head should contain allOf(
        InlineCallbackKeyboardButton("callback 1", "/callback1"),
        InlineUrlKeyboardButton("link 1", new URL("https://google.com"))
        )
      keyboard.inlineKeyboard.last should contain allOf(
        InlineUrlKeyboardButton("link 2", new URL("http://example.com")),
        InlineCallbackKeyboardButton("callback 2", "answer for callback 2")
        )
    }
    it("should create keyboard with one row of buttons") {
      // when:
      val keyboard = InlineKeyboard("button 1" -> 1, "button 2" -> "http://localhost")

      // then:
      keyboard.inlineKeyboard should have size 1
      keyboard.inlineKeyboard.head should contain allOf(
        InlineCallbackKeyboardButton("button 1", "1"),
        InlineUrlKeyboardButton("button 2", new URL("http://localhost"))
      )
    }
    it("should Option with keyboard") {
      // when:
      val keyboard = new InlineKeyboard().createOpt()

      // then:
      keyboard should contain(new InlineKeyboard().create())
    }
  }
}
