package ru.finagram

import ru.finagram.api.{ KeyboardButton, ReplyKeyboardMarkup }

import scala.collection.mutable

import ru.finagram.Keyboard._

/**
 * Simple builder for [[ru.finagram.api.ReplyKeyboardMarkup]].
 * // TODO describe constructor
 */
class Keyboard(parameters: Parameter*) {

  // binary mask describe parameters of the keyboard
  private val mask: Int = parameters.map(_.mask).fold(0)((m, p) => m | p)

  private val resizeKeyboard: Option[Boolean] = if (resize containedIn mask) Some(true) else None
  private val oneTimeKeyboard: Option[Boolean] = if (oneTime containedIn mask) Some(true) else None
  private val isSelective: Option[Boolean] = if (selective containedIn mask) Some(true) else None

  private val keyboards = mutable.Buffer[Seq[KeyboardButton]]()

  def buttons(row: String*): Keyboard = {
    keyboards += row.map(it => KeyboardButton(it))
    this
  }

  // TODO:
//  def buttons(row: KeyboardButton*): Keyboard = {
//    keyboards += row
//    this
//  }

  def create(): ReplyKeyboardMarkup = {
    ReplyKeyboardMarkup(keyboards.map(btn => btn), resizeKeyboard, oneTimeKeyboard, isSelective)
  }

  def createOpt(): Option[ReplyKeyboardMarkup] = Some(create())
}

object Keyboard {

  sealed trait Parameter {
    private[finagram] val mask: Int

    def containedIn(m: Int): Boolean = {
      if (mask.&(m) > 0) true else false
    }
  }

  object resize extends Parameter {
    private[finagram] val mask = 0x1
  }

  object oneTime extends Parameter {
    private[finagram] val mask = 0x2
  }

  object selective extends Parameter {
    private[finagram] val mask = 0x4
  }
}
