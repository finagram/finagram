package ru.finagram

import java.io.FileNotFoundException
import java.nio.file.Paths

import com.twitter.finagle.http.{ Message => _ }
import org.slf4j.LoggerFactory
import ru.finagram.FinagramBot.Handler
import ru.finagram.api._

import scala.collection.mutable
import scala.io.Source

/**
 * Trait for implementation of the bot logic.
 */
trait FinagramBot extends Polling {

  // Logic for handle messages from user
  val log = LoggerFactory.getLogger(getClass)

  private val handlers = mutable.Map[String, Handler]()

  /**
   * Token of the bot.
   */
  val token: String

  /**
   * Handle any errors.
   */
  def onError: PartialFunction[Throwable, Unit] = {
    case e: Throwable => log.error("", e)
  }

  /**
   * Add handle for specified text from user.
   * Every text should contain only one handle otherwise [[IllegalArgumentException]] will be thrown.
   *
   * @param text Text from user. Cannot be empty.
   * @param handler Logic for create answer for received text.
   */
  final def on(text: String)(handler: (Message) => Answer): Unit = {
    if (text.trim.isEmpty) {
      throw new IllegalArgumentException("Text cannot be empty")
    }
    if (handlers.contains(text)) {
      throw new IllegalArgumentException(s"Handler for command $text already registered.")
    }
    handlers(text) = handler
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
}

object FinagramBot {
  type Handler = (Message) => Answer
}

