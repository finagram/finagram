package ru.finagram

case class PollingException(offset: Long, e: Throwable) extends Exception(s"Polling was break on offset $offset", e)
