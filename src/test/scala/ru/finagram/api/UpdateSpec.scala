package ru.finagram.api

import ru.finagram.Spec

class UpdateSpec extends Spec {

  describe(s"extract ${classOf[Update]} from content with json string") {
    it("should create correct instance") {
      // given:
      val content =
        """
          |{
          |  "ok":true,
          |  "result":[{
          |    "update_id":217684839,
          |    "message":{
          |      "message_id":16,
          |      "from":{
          |        "id":192047269,
          |        "first_name":"Vladimir",
          |        "last_name":"Popov",
          |        "username":"dokwork_ru"
          |      },
          |      "chat":{
          |        "id":192047269,
          |        "first_name":"Vladimir",
          |        "last_name":"Popov",
          |        "username":"dokwork_ru",
          |        "type":"private"
          |      },
          |      "date":1468662330,
          |      "text":":)"
          |    }
          |  }]
          |}
          |
        """.stripMargin

      // when:
      val update = Update(content).get

      // then:
      update.updateId should be(217684839L)
      update.message should not be None
    }
  }
}
