package ru.finagram.api.json

import org.json4s.native.JsonMethods._
import org.scalatest.{ FreeSpec, Matchers }
import ru.finagram.api._
import ru.finagram.api.json.Implicit.formats
import ru.finagram.test.Utils

class TelegramResponseSerializerSpec extends FreeSpec with Matchers with Utils {

  s"parse json with Telegram error" - {
    s"should create instance of $TelegramException" in {
      // given:
      val content =
        """
          |{
          |   "ok":false,
          |   "description": "Something is wrong",
          |   "error_code": 1
          |}
        """.stripMargin

      // when:
      val error = parse(content).camelizeKeys.extract[TelegramResponse]

      // then:
      error should be(TelegramException(
        description = "Something is wrong",
        errorCode = Some(1)
      ))
    }
  }

  "parse empty response" - {
    "should create instance with empty result" in {
      // given:
      val content =
        """
          |{
          |   "ok":true,
          |   "result":[]
          |}
        """.stripMargin

      // when:
      val updates = parse(content).camelizeKeys.extract[TelegramResponse]

      // then:
      updates should be(Updates(
        result = Seq()
      ))
    }
  }

  "parse response with messages" - {
    s"should create instance of $Updates with two $MessageUpdate in result" in {
      // given:
      val content =
        """
          |{
          |   "ok":true,
          |   "result":[
          |    {
          |       "update_id":217684885,
          |       "message":{
          |          "message_id":82,
          |          "from":{
          |             "id":192047269,
          |             "first_name":"Vladimir",
          |             "last_name":"Popov"
          |          },
          |          "chat":{
          |             "id":192047269,
          |             "first_name":"Vladimir",
          |             "last_name":"Popov",
          |             "type":"private"
          |          },
          |          "date":1470854050,
          |          "text":"first"
          |       }
          |      },
          |      {
          |         "update_id":217684886,
          |         "message":{
          |            "message_id":83,
          |            "from":{
          |               "id":192047269,
          |               "first_name":"Vladimir",
          |               "last_name":"Popov"
          |            },
          |            "chat":{
          |               "id":192047269,
          |               "first_name":"Vladimir",
          |               "last_name":"Popov",
          |               "type":"private"
          |            },
          |            "date":1470854071,
          |            "text":"second"
          |         }
          |      }
          |   ]
          |}
        """.stripMargin

      // when:
      val updates = parse(content).camelizeKeys.extract[TelegramResponse]

      // then:
      updates should be(Updates(
        Seq(
          MessageUpdate(
            updateId = 217684885,
            message = TextMessage(
              messageId = 82,
              from = Some(User(192047269, "Vladimir", Some("Popov"))),
              date = 1470854050,
              chat = Chat(192047269, "private", None, Some("Vladimir"), Some("Popov")),
              text = "first"
            )
          ),
          MessageUpdate(
            updateId = 217684886,
            message = TextMessage(
              messageId = 83,
              from = Some(User(192047269, "Vladimir", Some("Popov"))),
              date = 1470854071,
              chat = Chat(192047269, "private", None, Some("Vladimir"), Some("Popov")),
              text = "second"
            )
          )
        )
      ))
    }
  }

  "parse response with callback query" - {
    s"should create instance of $Updates with $CallbackQueryUpdate" in {
      // given:
      val content =
        """
          |{
          |   "ok":true,
          |   "result":[
          |      {
          |         "update_id":353441033,
          |         "callback_query":{
          |            "id":"824836741025835269",
          |            "from":{
          |               "id":192047269,
          |               "first_name":"Vladimir",
          |               "last_name":"Popov",
          |            },
          |            "message":{
          |               "message_id":175,
          |               "from":{
          |                  "id":255752647,
          |                  "first_name":"example"
          |               },
          |               "chat":{
          |                  "id":192047269,
          |                  "first_name":"Vladimir",
          |                  "last_name":"Popov",
          |                  "type":"private"
          |               },
          |               "date":1473608617,
          |               "text":"Keyboard"
          |            },
          |            "data":"some text data"
          |         }
          |      }
          |   ]
          |}
        """.stripMargin

      // when:
      val updates = parse(content).camelizeKeys.extract[TelegramResponse]

      // then:
      updates should be(Updates(
        Seq(
          CallbackQueryUpdate(
            updateId = 353441033,
            callbackQuery = CallbackQuery(
              id = "824836741025835269",
              from = User(192047269, "Vladimir", Some("Popov")),
              message = Some(TextMessage(
                175L,
                Some(User(255752647, "example")),
                1473608617L,
                Chat(192047269, "private", firstName = Some("Vladimir"), lastName = Some("Popov")),
                "Keyboard"
              )),
              data = "some text data"
            )
          )
        )
      ))
    }
  }

  "parse response with file" - {
    s"should create instance of ${classOf[FileResponse]}" in {
      // given:
      val fileId = "BQADAgADEccDpWhyC39ABCCdtF"
      val fileSize = Some(8090)
      val filePath = Some("file.png")
      val content =
        s"""
           |{
           |   "ok":true,
           |   "result":{
           |      "file_id":"$fileId",
           |      "file_size":${fileSize.get},
           |      "file_path":"${filePath.get}"
           |   }
           |}
        """.stripMargin

      // when:
      val response = parse(content).camelizeKeys.extract[TelegramResponse]

      // then:
      response should be(FileResponse(
        result = File(fileId, fileSize, filePath)
      ))
    }
  }
}
