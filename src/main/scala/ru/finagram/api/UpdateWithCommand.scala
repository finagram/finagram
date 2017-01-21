package ru.finagram.api

class UpdateWithCommand(update: Update) extends Update {
  override val updateId: Long = update.updateId

  val command = update match {
    case MessageUpdate(_, message: TextMessage) => message.text
    case CallbackQueryUpdate(_, callback) => callback.data
    case notExpectedUpdate => throw new RuntimeException("Not expected update " + notExpectedUpdate)
  }
}

object UpdateWithCommand {

  def apply(update: Update): Boolean = update match {
    case MessageUpdate(_, _: TextMessage) => true
    case _: CallbackQueryUpdate => true
    case _ => false
  }

  def unapply(arg: Update): Option[UpdateWithCommand] = if (apply(arg)) Some(new UpdateWithCommand(arg)) else None
}