package ru.finagram.api

import org.json4s.Extraction
import org.json4s.native.JsonMethods._
import org.scalatest.{ FreeSpec, Matchers }
import ru.finagram.test.Utils
import ru.finagram.test.matchers.Json

import scala.util.Random

class AnswerSerializationSpec extends FreeSpec with Matchers with Utils {

  import ru.finagram.api.json.Implicit.formats

  "serialization" - {
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
          Json(
            s"""
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
          Json(
            s"""
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
          Json(
            s"""
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

  "deserialization" - {
    "when deserialize text answer from json" - {
      "should be created expected object" in {
        // given:
        val id = Random.nextLong()
        val text = Random.nextString(12)
        val reply = Random.nextLong()

        // when:
        val result = parse(
          s"""
           {
             "chat_id": $id,
             "reply_markup": {
               "keyboard": [[{"text": "1"}, {"text": "2"}]]
             },
             "disable_notification": true,
             "disable_web_page_preview": true,
             "reply_to_message_id": $reply,
             "text": "$text",
             "parse_mode": "Markdown"
           }
        """).camelizeKeys.extract[MarkdownAnswer]

        // then:
        result should be(MarkdownAnswer(
          chatId = id,
          text = text,
          replyMarkup = new Keyboard().buttons("1", "2").createOpt(),
          disableNotification = Some(true),
          disableWebPagePreview = Some(true),
          replyToMessageId = Some(reply)
        ))
      }
    }
    "when deserialize photo answer from json" - {
      "should be created expected object" in {
        // given:
        val chatId = Random.nextLong()
        val photo = Random.nextString(5)
        val caption = Some(Random.nextString(5))

        // when:
        val result = parse(
          s"""
            {
               "chat_id": $chatId,
               "photo": "$photo",
               "caption": "${caption.get}"
            }
          """).camelizeKeys.extract[PhotoAnswer]

        // then:
        result should be(PhotoAnswer(
          chatId = chatId,
          photo = photo,
          caption = caption
        ))
      }
    }
    "when deserialize sticker answer from json" - {
      "should be created expected object" in {
        // given:
        val chatId = Random.nextLong()
        val sticker = Random.nextString(5)

        // when:
        val result = parse(
          s"""
            {
               "chat_id": $chatId,
               "sticker": "$sticker"
            }
          """).camelizeKeys.extract[StickerAnswer]

        // then:
        result should be(StickerAnswer(
          chatId = chatId,
          sticker = sticker
        ))
      }
    }
  }
}
