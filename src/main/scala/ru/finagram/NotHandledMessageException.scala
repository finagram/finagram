package ru.finagram

case class NotHandledMessageException(msg: String) extends Exception(msg)
