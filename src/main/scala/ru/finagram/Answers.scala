package ru.finagram

import java.io.FileNotFoundException
import java.nio.file.Paths

import com.twitter.util.Future
import ru.finagram.!!!
import ru.finagram.api._

import scala.io.Source


object Answers {

  final def text(text: String, keyboard: Option[KeyboardMarkup] = None)(update: Update) = Future {
    FlatAnswer(
      chatId = extractChatId(update),
      text = text,
      keyboard
    )
  }

  final def markdown(text: String, keyboard: Option[KeyboardMarkup] = None)(update: Update) = Future {
    MarkdownAnswer(
      chatId = extractChatId(update),
      text = text,
      keyboard
    )
  }

  final def html(text: String, keyboard: Option[KeyboardMarkup] = None)(update: Update) = Future {
    HtmlAnswer(
      chatId = extractChatId(update),
      text = text,
      keyboard
    )
  }

  final def photo(photo: String, caption: Option[String] = None, keyboard: Option[KeyboardMarkup] = None)(update: Update) = Future {
    PhotoAnswer(
      chatId = extractChatId(update),
      photo = photo,
      caption = caption,
      keyboard
    )
  }

  final def sticker(sticker: String, keyboard: Option[KeyboardMarkup] = None)(update: Update) = Future {
    StickerAnswer(
      chatId = extractChatId(update),
      sticker = sticker,
      keyboard
    )
  }

  /**
   * Read all from resources (if resource with specified path exists) or from file
   * and return text as [[String]].
   *
   * @param path path to resource or file.
   * @return text from resource or file.
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

  private def extractChatId: PartialFunction[Update, Long] = {
    case MessageUpdate(_, message: Message) =>
      message.chat.id

    case CallbackQueryUpdate(_, callback) =>
      callback.from.id

    case v => !!!(s"Not implemented extractChatId for $v")
  }
}
