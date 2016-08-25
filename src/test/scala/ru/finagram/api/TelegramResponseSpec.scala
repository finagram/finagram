package ru.finagram.api

import ru.finagram.util.Spec

class TelegramResponseSpec extends Spec {

  describe("parse correct response from Telegram") {
    it(s"should create instance of $Updates") {
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
          |             "last_name":"Popov",
          |             "username":"dokwork_ru"
          |          },
          |          "chat":{
          |             "id":192047269,
          |             "first_name":"Vladimir",
          |             "last_name":"Popov",
          |             "username":"dokwork_ru",
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
          |               "last_name":"Popov",
          |               "username":"dokwork_ru"
          |            },
          |            "chat":{
          |               "id":192047269,
          |               "first_name":"Vladimir",
          |               "last_name":"Popov",
          |               "username":"dokwork_ru",
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
      val updates = TelegramResponse(content).asInstanceOf[Updates]

      // then:
      updates.ok should be(true)
      updates.result should have size 2
      updates.result.head.updateId should be(217684885)
      updates.result.head.message.get should be(TextMessage(
        messageId = 82,
        from = Some(User(192047269, "Vladimir", Some("Popov"), Some("dokwork_ru"))),
        date = 1470854050,
        chat = Chat(192047269, "private", None, Some("Vladimir"), Some("Popov"), Some("dokwork_ru")),
        text = "first"
      ))
      updates.result.last.updateId should be(217684886)
      updates.result.last.message.get should be(TextMessage(
        messageId = 83,
        from = Some(User(192047269, "Vladimir", Some("Popov"), Some("dokwork_ru"))),
        date = 1470854071,
        chat = Chat(192047269, "private", None, Some("Vladimir"), Some("Popov"), Some("dokwork_ru")),
        text = "second"
      ))
    }
  }
  describe("parse response with error from Telegram") {
    it(s"should create instance of $TelegramException") {
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
      val error = TelegramResponse(content).asInstanceOf[TelegramException]

      // then:
      error.ok should be(false)
      error.description should be("Something is wrong")
      error.errorCode should be(Some(1))
    }
  }
}
