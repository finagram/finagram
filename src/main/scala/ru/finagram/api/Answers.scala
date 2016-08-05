package ru.finagram.api

import java.io.FileNotFoundException
import java.nio.file.Paths

import scala.io.Source

trait Answers {

  /**
   * Read all from resources (if resource with specified path exists) or from file
   * and return content as [[String]].
   *
   * @param path path to resource or file.
   * @return content from resource or file.
   */
  final def from(path: String): String = {
    val source = Option(getClass.getResource(path)) match {
      case Some(url) =>
        Source.fromURL(url)
      case None if Paths.get(path).toFile.exists() =>
        Source.fromFile(path)
      case _ =>
        throw new FileNotFoundException(s"No resource or file was not found by path $path")
    }
    source.mkString
  }

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
