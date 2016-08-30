package ru.finagram

import ru.finagram.api.{ KeyboardButton, ReplyKeyboardMarkup }

import scala.collection.mutable

/**
 * Simple builder for [[ru.finagram.api.ReplyKeyboardMarkup]].
 */
class Keyboard {

  private val keyboards = mutable.Buffer[Seq[KeyboardButton]]()
  private var resizeKeyboard: Option[Boolean] = None
  private var oneTimeKeyboard: Option[Boolean] = None
  private var isSelective: Option[Boolean] = None

  def buttons(row: String*): Keyboard = {
    keyboards += row.map(it => KeyboardButton(it))
    this
  }

//  def buttons(row: KeyboardButton*): Keyboard = {
//    keyboards += row
//    this
//  }

  def resize(arg: Boolean): Keyboard = {
    resizeKeyboard = Some(arg)
    this
  }

  def oneTime(arg: Boolean): Keyboard = {
    oneTimeKeyboard = Some(arg)
    this
  }

  def selective(arg: Boolean): Keyboard = {
    isSelective = Some(arg)
    this
  }

  def create(): ReplyKeyboardMarkup = {
    ReplyKeyboardMarkup(keyboards.toSeq, resizeKeyboard, oneTimeKeyboard, isSelective)
  }
}
