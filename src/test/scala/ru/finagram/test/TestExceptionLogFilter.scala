package ru.finagram.test

import ch.qos.logback.classic.spi.{ ILoggingEvent, IThrowableProxy }
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply

class TestExceptionLogFilter extends Filter[ILoggingEvent] {

  override def decide(event: ILoggingEvent): FilterReply = {
    if (isTestException(event.getThrowableProxy)) {
      FilterReply.DENY
    } else {
      FilterReply.NEUTRAL
    }
  }

  private val testExceptionName: String = classOf[TestException].getCanonicalName

  private def isTestException(proxy: IThrowableProxy): Boolean = {
    (proxy != null) && (proxy.getClassName == testExceptionName)
  }
}
