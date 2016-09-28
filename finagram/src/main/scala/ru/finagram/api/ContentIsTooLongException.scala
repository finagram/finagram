package ru.finagram.api

case class ContentIsTooLongException(length: Long) extends Exception(s"Content length should be less than 4096, but was $length")
