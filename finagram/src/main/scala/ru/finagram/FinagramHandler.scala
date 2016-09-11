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
   * Every command should contain only one handle otherwise [[IllegalArgumentException]] will be thrown.
   *
   * @param commands Commands from user. It should contains at least one command.
   *                 Every command cannot be blank string.
   * @param handler Logic for create answer for received command from list.
   */
  final def on(commands: String*)(handler: (Message) => Answer): Unit = {
    if (commands.isEmpty) {
      throw new IllegalArgumentException("Commands list cannot be empty.")
    }
    commands.foreach { command =>
      if (command.trim.isEmpty) {
        throw new IllegalArgumentException("Command cannot be blank")
      }
      if (handlers.contains(command)) {
        throw new IllegalArgumentException(s"Handler for command $command already registered.")
      }
      handlers(command) = handler
    }
  }
}
