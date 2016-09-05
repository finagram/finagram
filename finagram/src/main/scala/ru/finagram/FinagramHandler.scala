package ru.finagram

import ru.finagram.api.{ Answer, Message }

import scala.collection.mutable

/**
 * Trait for describe message handlers.
 */
trait FinagramHandler {

  private[finagram] val handlers: mutable.Map[String, (Message) => Answer]

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
}
