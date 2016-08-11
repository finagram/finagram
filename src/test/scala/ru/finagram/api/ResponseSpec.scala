package ru.finagram.api

import ru.finagram.Spec

class ResponseSpec extends Spec {

  describe("parse correct response from Telegram") {
    it(s"should create instance of $Updates") {
      // given:
      val content =
        """
          |{
          |   "ok":true,
          |   "result":[
          |      {
          |         "update_id":217684880,
          |         "message":{  }
          |      },
          |      {
          |         "update_id":217684882,
          |         "message":{
          |            "message_id":79,
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
          |            "date":1470854044,
          |            "text":"first"
          |         }
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
      val updates = Response(content).asInstanceOf[Updates]

      // then:
      updates.ok should be(true)
      updates.result should have size 3
      updates.result.head should be(Update(217684880L, None))
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
        """.stripMargin

      // when:
      val error = Response(content).asInstanceOf[TelegramException]

      // then:
      error.ok should be(false)
      error.description should be("Something is wrong")
      error.errorCode should be(Some(1))
    }
  }
}
