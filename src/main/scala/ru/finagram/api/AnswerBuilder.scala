package ru.finagram.api

import com.twitter.util.Future

object AnswerBuilder {

  final def text(text: String, disableNotification: Option[Boolean] = None)(message: Message) = Future(
    FlatAnswer(
      chatId = message.chat.id,
      text = text,
      disableNotification = disableNotification
    )
  )

  final def markdown(text: String, disableNotification: Option[Boolean] = None)(message: Message) = Future(
    MarkdownAnswer(
      chatId = message.chat.id,
      text = text,
      disableNotification = disableNotification
    )
  )

  final def html(text: String, disableNotification: Option[Boolean] = None)(message: Message) = Future(
    HtmlAnswer(
      chatId = message.chat.id,
      text = text,
      disableNotification = disableNotification
    )
  )

  final def photo(photo: String, caption: Option[String] = None, disableNotification: Option[Boolean] = None)(message: Message) = Future(
    PhotoAnswer(
      chatId = message.chat.id,
      photo = photo,
      caption = caption,
      disableNotification = disableNotification
    )
  )

  final def sticker(sticker: String, disableNotification: Option[Boolean] = None)(message: Message) = Future(
    StickerAnswer(
      chatId = message.chat.id,
      sticker = sticker,
      disableNotification = disableNotification
    )
  )
}
