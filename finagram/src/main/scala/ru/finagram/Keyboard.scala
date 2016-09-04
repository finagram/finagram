package ru.finagram

import ru.finagram.api.{ KeyboardButton, ReplyKeyboardMarkup }

import scala.collection.mutable

/**
 * Simple builder for [[ru.finagram.api.ReplyKeyboardMarkup]].
 */
class Keyboard {

  // TODO new Keyboard(resize, oneTime)

  private val keyboards = mutable.Buffer[Seq[KeyboardButton]]()
  private var resizeKeyboard: Option[Boolean] = None
  private var oneTimeKeyboard: Option[Boolean] = None
  private var isSelective: Option[Boolean] = None

  def buttons(row: String*): Keyboard = {
    keyboards += row.map(it => KeyboardButton(it))
    this
  }

  // TODO:
//  def buttons(row: KeyboardButton*): Keyboard = {
//    keyboards += row
//    this
//  }

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

  def create(): ReplyKeyboardMarkup = {
    ReplyKeyboardMarkup(keyboards.map(btn => btn), resizeKeyboard, oneTimeKeyboard, isSelective)
  }

  def createOpt(): Option[ReplyKeyboardMarkup] = Some(create())
}
