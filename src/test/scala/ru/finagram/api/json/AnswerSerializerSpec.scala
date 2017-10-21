package ru.finagram.api.json

import org.json4s.native.JsonMethods._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{ Arbitrary, Gen }
import org.scalatest.prop.PropertyChecks
import org.scalatest.{ FreeSpec, Matchers }
import ru.finagram.api._
import ru.finagram.test.MockitoSugar
import ru.finagram.test.arbitraries.Arbitraries
import ru.finagram.test.matchers.Json

class AnswerSerializerSpec extends FreeSpec
  with Matchers
  with MockitoSugar
  with PropertyChecks
  with Arbitraries {

  import ru.finagram.api.json.Implicit.formats

  "serialization" - {
    "when serializing text answer to json" - {
      "should be created json object with only the initialized fields" in forAll {
        // given:
        (id: Long, text: String) =>
          val answer = FlatAnswer(id, text)

          // when:
          val result = compactWrite(answer)

          // then:
          result should be(
            Json( s"""{ "chat_id": $id, "text": "$text" }""")
          )
      }
    }
    "should be created json object with all initialized fields" in forAll {
      // given:
      (id: Long, text: String, reply: Long) =>
        val answer = FlatAnswer(
          chatId = id,
          text = text,
          replyMarkup = new Keyboard().buttons("1", "2").createOpt(),
          disableNotification = Some(true),
          disableWebPagePreview = Some(true),
          replyToMessageId = Some(reply)
        )

        // when:
        val result = compactWrite(answer)

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
    "should be created json object with expected parse_mode" in forAll {
      // given:
      (id: Long, text: String) =>
        val answer = HtmlAnswer(id, text)

        // when:
        val result = compactWrite(answer)

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
    implicit val arb: Arbitrary[Option[String]] = Arbitrary {
      Gen.some(arbitrary[String])
    }
    "should be created correct json object" in forAll {
      // given:
      (chatId: Long, photo: String, caption: Option[String]) =>
        val answer = PhotoAnswer(chatId, photo, caption)

        // when:
        val result = compactWrite(answer)

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
    "should be created correct json object" in forAll {
      // given:
      (chatId: Long, sticker: String) =>
        val answer = StickerAnswer(chatId, sticker)

        // when:
        val result = compactWrite(answer)

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

  "deserialization" - {
    "when deserialize text answer from json" - {
      "should be created expected object" in forAll {
        // given:
        (id: Long, text: String, reply: Long) =>

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
        """).camelizeKeys.extract[Answer]

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
      implicit val arb: Arbitrary[Option[String]] = Arbitrary {
        Gen.some(arbitrary[String])
      }
      "should be created expected object" in forAll {
        // given:
        (chatId: Long, photo: String, caption: Option[String]) =>

          // when:
          val result = parse(
            s"""
            {
               "chat_id": $chatId,
               "photo": "$photo",
               "caption": "${caption.get}"
            }
          """).camelizeKeys.extract[Answer]

          // then:
          result should be(PhotoAnswer(
            chatId = chatId,
            photo = photo,
            caption = caption
          ))
      }
    }
    "when deserialize sticker answer from json" - {
      "should be created expected object" in forAll {
        // given:
        (chatId: Long, sticker: String) =>

        // when:
        val result = parse(
          s"""
            {
               "chat_id": $chatId,
               "sticker": "$sticker"
            }
          """).camelizeKeys.extract[Answer]

        // then:
        result should be(StickerAnswer(
          chatId = chatId,
          sticker = sticker
        ))
      }
    }
  }
}
