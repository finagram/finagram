package ru.finagram.api

import ru.finagram.util.Spec

class InlineKeyboardSpec extends Spec {

  describe("create inline keyboard") {
    it(s"should create ${classOf[InlineKeyboard]} with two rows of callback and url buttons") {
      // when:
      val keyboard: InlineKeyboardMarkup = new InlineKeyboard()
        .buttons("callback 1" -> "/callback1", "link 1" -> "https://google.com")
        .buttons("link 2" -> "http://example.com", "callback 2" -> "answer for callback 2")
        .create()

      // then:
      keyboard.inlineKeyboard should have size (2)
      keyboard.inlineKeyboard.head should contain allOf(
        InlineCallbackKeyboardButton("callback 1", "/callback1"),
        InlineUrlKeyboardButton("link 1", "https://google.com")
        )
      keyboard.inlineKeyboard.last should contain allOf(
        InlineUrlKeyboardButton("link 2", "http://example.com"),
        InlineCallbackKeyboardButton("callback 2", "answer for callback 2")
        )
    }
  }
}
