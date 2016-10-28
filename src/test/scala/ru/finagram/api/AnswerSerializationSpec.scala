package ru.finagram.api

import org.json4s.Extraction
import org.scalatest.{ FreeSpec, Matchers }
import ru.finagram.api.json.Implicit.formats
import ru.finagram.test.Utils
import ru.finagram.test.matchers.Json

import scala.util.Random

class AnswerSerializationSpec extends FreeSpec with Matchers with Utils {

  "when serializing text answer to json" - {
    "should be created json object with only the initialized fields" in {
      // given:
      val id = Random.nextLong()
      val text = Random.nextString(12)
      val answer = FlatAnswer(id, text)

      // when:
      val result = Extraction.decompose(answer).snakizeKeys

      // then:
      result should be(
        Json(s"""
           {
             "chat_id": $id,
             "text": "$text"
           }
        """)
      )
    }
    "should be created json object with all initialized fields" in {
      // given:
      val id = Random.nextLong()
      val text = Random.nextString(12)
      val reply = Random.nextLong()
      val answer = FlatAnswer(
        chatId = id,
        text = text,
        replyMarkup = new Keyboard().buttons("1", "2").createOpt(),
        disableNotification = Some(true),
        disableWebPagePreview = Some(true),
        replyToMessageId = Some(reply)
      )

      // when:
      val result = Extraction.decompose(answer).snakizeKeys

      // then:
      result should be(
        Json(s"""
           {
             "chat_id": $id,
             "reply_markup": {
               "keyboard": [[{"text": "1"}, {"text": "2"}]]
             },
             "disable_notification": true,
             "disable_web_page_preview": true,
             "reply_to_message_id": $reply,
             "text": "$text"
           }
        """)
      )
    }
    "should be created json object with expected parse_mode" in {
      // given:
      val id = Random.nextLong()
      val text = Random.nextString(12)
      val answer = HtmlAnswer(id, text)

      // when:
      val result = Extraction.decompose(answer).snakizeKeys

      // then:
      result should be(
        Json(s"""
           {
             "chat_id": $id,
             "text": "$text",
             "parse_mode": "HTML"
           }
        """)
      )
    }
  }

  "when serialize photo answer to json" - {
    "should be created correct json object" in {
      // given:
      val answer = PhotoAnswer(
        chatId = Random.nextLong(),
        photo = Random.nextString(5),
        caption = Some(Random.nextString(5))
      )

      // when:
      val result = Extraction.decompose(answer).snakizeKeys

      // then:
      result should be(
        Json(
          s"""
             {
                "chat_id": ${answer.chatId},
                "photo": "${answer.photo}",
                "caption": "${answer.caption.get}"
             }
          """
        )
      )
    }
  }

  "when serialize sticker answer to json" - {
    "should be created correct json object" in {
      // given:
      val answer = StickerAnswer(
        chatId = Random.nextLong(),
        sticker = Random.nextString(5)
      )

      // when:
      val result = Extraction.decompose(answer).snakizeKeys

      // then:
      result should be(
        Json(
          s"""
             {
                "chat_id": ${answer.chatId},
                "sticker": "${answer.sticker}"
             }
          """
        )
      )
    }
  }
}
