package ru.finagram.api

import scala.collection.mutable


/**
 * Simple builder for [[ru.finagram.api.ReplyKeyboardMarkup]].
 * // TODO describe constructor
 */
class Keyboard {

  private var resizeKeyboard: Option[Boolean] = None
  private var oneTimeKeyboard: Option[Boolean] = None
  private var isSelective: Option[Boolean] = None

  private val keyboards = mutable.Buffer[Seq[KeyboardButton]]()

  def resize(): Keyboard = {
    resizeKeyboard = Some(true)
    this
  }

  def oneTime(): Keyboard = {
    oneTimeKeyboard = Some(true)
    this
  }

  def selective(): Keyboard = {
    isSelective = Some(true)
    this
  }

  def buttons(row: String*): Keyboard = {
    buttons(row.map(it => KeyboardButton(it)))
  }

  def buttons(row: => Seq[KeyboardButton]): Keyboard = {
    keyboards += row
    this
  }

  def create(): ReplyKeyboardMarkup = {
    ReplyKeyboardMarkup(keyboards.map(btn => btn), resizeKeyboard, oneTimeKeyboard, isSelective)
  }

  def createOpt(): Option[ReplyKeyboardMarkup] = Some(create())
}
