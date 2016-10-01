package ru.finagram.api

import java.net.{ MalformedURLException, URL }

import scala.collection.mutable

class InlineKeyboard {

  private val keyboards = mutable.Buffer[Seq[InlineKeyboardButton]]()

  @throws(clazz = classOf[MalformedURLException])
  def buttons(row: (String, Any)*): InlineKeyboard = {
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

  @throws(clazz = classOf[MalformedURLException])
  private def buttonFromTuple(tuple: (String, Any)): InlineKeyboardButton = {
    val text = tuple._1
    val data = tuple._2.toString
    val answer = data.toLowerCase
    if (answer.startsWith("http://") || answer.startsWith("https://")) {
      InlineUrlKeyboardButton(text, new URL(data))
    } else {
      InlineCallbackKeyboardButton(text, data)
    }
  }
}

object InlineKeyboard {

  @throws(clazz = classOf[MalformedURLException])
  def apply(row: (String, Any)*): InlineKeyboardMarkup = {
    new InlineKeyboard()
      .buttons(row: _*)
      .create()
  }
}
