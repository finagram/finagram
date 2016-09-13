package ru.finagram.tutorial

import java.util.{ Locale, ResourceBundle }

object Content {

  val not_supported = "Пока не реализовано"

   val start =
    """
      |*Finagram Tutorial Bot*
      |
      |Данный бот поможет Вам изучить [Finagram](https://github.com/finagram/finagram) -библиотеку
      |для написания ботов к Telegram.
    """.stripMargin

   val tutorial =
    """
      |*Tutorial*
      |
      |Привет! Я здесь, чтобы рассказать тебе как пользоваться `Finagram`.
      |
      |Шаг за шагом я покажу как создать своего бота и постараюсь максимально подробно ответить на все
      |возникающие вопросы. Готов?
      |
    """.stripMargin

   val select_message_receiver =
     """
       |*Как получить сообщения из Telegram*
       |
       |Telegram поддерживает два способа получения новых сообщений:
       |1. Polling /polling
       |2. Webhooks /webhooks
     """.stripMargin
}

