package ru.finagram.api

/**
 * This trait represents an incoming update.
 */
sealed trait Update {
  val updateId: Long
}

/**
 * This object represents an incoming update with [[Message]].
 *
 * @param updateId  The update‘s unique identifier.
 *                  Update identifiers start from a certain positive number and increase sequentially.
 * @param message   New incoming message of any kind — text, photo, sticker, etc.
 */
case class MessageUpdate(updateId: Long, message: Message) extends Update

/**
 * This object represents an incoming update with [[CallbackQuery]].
 *
 * @param updateId The update‘s unique identifier.
 * @param callbackQuery New incoming callback query.
 */
case class CallbackQueryUpdate(updateId: Long, callbackQuery: CallbackQuery) extends Update