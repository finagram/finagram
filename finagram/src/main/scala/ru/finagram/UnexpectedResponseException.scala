package ru.finagram

class UnexpectedResponseException(msg: String, e: Throwable = null) extends Exception(msg, e)