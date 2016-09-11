package ru.finagram

import java.io.FileNotFoundException
import java.nio.file.Paths

import ru.finagram.api._

import scala.io.Source


object Answers {

  final def text(text: String, keyboard: Option[ReplyKeyboardMarkup] = None)(message: Message) = {
    FlatAnswer(
      chatId = message.chat.id,
      content = text,
      keyboard
    )
  }

  final def markdown(text: String, keyboard: Option[ReplyKeyboardMarkup] = None)(message: Message) = {
    MarkdownAnswer(
      chatId = message.chat.id,
      content = text,
      keyboard
    )
  }

  final def html(text: String, keyboard: Option[ReplyKeyboardMarkup] = None)(message: Message) = {
    HtmlAnswer(
      chatId = message.chat.id,
      content = text,
      keyboard
    )
  }

  final def photo(photo: String, caption: Option[String] = None, keyboard: Option[ReplyKeyboardMarkup] = None)(message: Message) = {
    PhotoAnswer(
      chatId = message.chat.id,
      photo = photo,
      caption = caption,
      keyboard
    )
  }

  final def sticker(sticker: String, keyboard: Option[ReplyKeyboardMarkup] = None)(message: Message) = {
    StickerAnswer(
      chatId = message.chat.id,
      sticker = sticker,
      keyboard
    )
  }

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

  final def code(code: String, lang: String = "java") = {
    s"```$lang\n$code\n```"
  }
}
