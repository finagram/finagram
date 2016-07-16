package ru.finagram.api

import com.twitter.util.Future

object ReplayBuilder {

  final def text(text: String, disableNotification: Option[Boolean] = None)(message: Message) = Future(
    FlatAnswer(
      chatId = message.chat.id,
      text = text,
      disableNotification = disableNotification,
      replyToMessageId = Some(message.messageId)
    )
  )
}
