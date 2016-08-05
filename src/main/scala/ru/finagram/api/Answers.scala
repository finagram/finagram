package ru.finagram.api

trait Answers {

  final def text(text: String, disableNotification: Option[Boolean] = None)(message: Message) = {
    FlatAnswer(
      chatId = message.chat.id,
      text = text,
      disableNotification = disableNotification
    )
  }

  final def markdown(text: String, disableNotification: Option[Boolean] = None)(message: Message) = {
    MarkdownAnswer(
      chatId = message.chat.id,
      text = text,
      disableNotification = disableNotification
    )
  }

  final def html(text: String, disableNotification: Option[Boolean] = None)(message: Message) = {
    HtmlAnswer(
      chatId = message.chat.id,
      text = text,
      disableNotification = disableNotification
    )
  }

  final def photo(photo: String, caption: Option[String] = None, disableNotification: Option[Boolean] = None)(message: Message) = {
    PhotoAnswer(
      chatId = message.chat.id,
      photo = photo,
      caption = caption,
      disableNotification = disableNotification
    )
  }

  final def sticker(sticker: String, disableNotification: Option[Boolean] = None)(message: Message) = {
    StickerAnswer(
      chatId = message.chat.id,
      sticker = sticker,
      disableNotification = disableNotification
    )
  }
}
