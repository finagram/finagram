package ru.finagram.api

import java.net.URL

import org.scalatest.{ FreeSpec, Matchers }
import ru.finagram.test.Utils

class InlineKeyboardSpec extends FreeSpec with Matchers with Utils {

  "create inline keyboard" - {
    s"should create ${classOf[InlineKeyboard]} with two rows of callback and url buttons" in {
      // when:
      val keyboard: InlineKeyboardMarkup = new InlineKeyboard()
        .buttons("callback 1" -> "/callback1", "link 1" -> "https://google.com")
        .buttons("link 2" -> new URL("http://example.com"), "callback 2" -> "answer for callback 2")
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
    "should create keyboard with one row of buttons" in {
      // when:
      val keyboard = InlineKeyboard("button 1" -> 1, "button 2" -> "http://localhost")

      // then:
      keyboard.inlineKeyboard should have size 1
      keyboard.inlineKeyboard.head should contain allOf(
        InlineCallbackKeyboardButton("button 1", "1"),
        InlineUrlKeyboardButton("button 2", "http://localhost")
      )
    }
    "should Option with keyboard" in {
      // when:
      val keyboard = new InlineKeyboard().createOpt()

      // then:
      keyboard should contain(new InlineKeyboard().create())
    }
  }
}
