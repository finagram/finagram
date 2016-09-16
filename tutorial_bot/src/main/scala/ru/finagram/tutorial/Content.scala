package ru.finagram.tutorial

import java.util.{ Locale, ResourceBundle }

object Content {

  val not_supported = "Пока не реализовано"

  val start =
    """
      |*Finagram Tutorial Bot*
      |
      |Привет! Я здесь, чтобы рассказать тебе как пользоваться [Finagram](https://github.com/finagram/finagram) - библиотекой для написания ботов к Telegram.
      |
      |Шаг за шагом я покажу как создать своего бота и постараюсь максимально подробно ответить на все возникающие вопросы.
      |Готов?
      |
    """.stripMargin

  val select_message_receiver =
    """
      |*Как получить сообщения из Telegram*
      |
      |Telegram поддерживает два способа получения новых сообщений:
      |1. [Polling](https://core.telegram.org/bots/api#getupdates)
      |2. [Webhooks](https://core.telegram.org/bots/api#setwebhook)
      |
      |Каждый из подходов реализован в соответствующем трейте:
    """.stripMargin

  val polling =
    """
      |*Polling*
      |
      |Получение новых сообщений через постоянный опрос Telegram реализован в трейте `ru.finagram.Polling`.
      |
      |Данный трейт требует определение метода `handle` принимающего
      |полученное обновление и возвращающего Future с опциональным ответом:
      |```
      |def handle(update: Update): Future[Option[Answer]]
      |```
      |Периодичность обращений к серверу определяется значением метода `timeout` и поумолчанию равно 700 миллисекунд.
      |
      |Этого интерфейса в совокупности с /client к Telegram достаточно для реализации бота.
      |
    """.stripMargin

  val handler =
    """
      |*Обработчик обновлений*
      |
      |Мостом между обновлением и ответом на него выступает `handler`. Это функция, принимающая на вход
      |обновление и возвращающая Future с ответом:
      |```
      |(Update) => Future[Answer]
      |```
      |
    """.stripMargin
}

