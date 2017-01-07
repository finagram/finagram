package ru.finagram

import com.twitter.util.Future
import ru.finagram.api.{ Answer, Message, MessageUpdate, Update }

import scala.collection.mutable
import scala.reflect.ClassTag

/**
 * Trait for describe message handlers.
 */
trait FinagramHandler {

  private[finagram] val commandHandlers: mutable.Map[String, (Update) => Future[Answer]]
  private[finagram] val messageHandlers: mutable.Set[PartialFunction[MessageUpdate, Future[Answer]]]

  /**
   * Add handler for specified text from user.
   * Every command should contain only one handle otherwise [[IllegalArgumentException]] will be thrown.
   *
   * @param commands Commands from user. It should contains at least one command.
   *                 Every command cannot be blank string and should begin from /.
   * @param handler Logic for create answer for received command from list.
   */
  final def on(commands: String*)(handler: (Update) => Future[Answer]): Unit = {
    if (commands.isEmpty) {
      throw new IllegalArgumentException("Commands list cannot be empty.")
    }
    commands.foreach { command =>
      if (command.trim.isEmpty) {
        throw new IllegalArgumentException("Command cannot be blank")
      }
      if (commandHandlers.contains(command)) {
        throw new IllegalArgumentException(s"Handler for command $command already registered.")
      }
      commandHandlers(command) = handler
    }
  }

  final def on[T <: Message](handler: (MessageUpdate) => Future[Answer])(implicit classTag: ClassTag[T]): Unit = {
    val function: PartialFunction[MessageUpdate, Future[Answer]] = {
      case u @ MessageUpdate(_, message) if classTag.runtimeClass == message.getClass =>
        handler(u)
    }
    messageHandlers.add(function)
  }
}
