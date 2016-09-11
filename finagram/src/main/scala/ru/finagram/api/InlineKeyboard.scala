package ru.finagram.api

import scala.collection.mutable

class InlineKeyboard {

  private val keyboards = mutable.Buffer[Seq[InlineKeyboardButton]]()

  def buttons(row: (String, String)*): InlineKeyboard = {
    buttons(row.map(buttonFromTuple))
  }

  def buttons(row: => Seq[InlineKeyboardButton]): InlineKeyboard = {
    keyboards += row
    this
  }

  def create(): InlineKeyboardMarkup = {
    InlineKeyboardMarkup(keyboards.map(btn => btn))
  }

  def createOpt(): Option[InlineKeyboardMarkup] = Some(create())

  private def buttonFromTuple(tuple: (String, String)): InlineKeyboardButton = {
    val answer = tuple._2.toLowerCase
    if (answer.startsWith("http://") || answer.startsWith("https://")) {
      InlineUrlKeyboardButton(tuple._1, tuple._2)
    } else {
      InlineCallbackKeyboardButton(tuple._1, tuple._2)
    }
  }
}
