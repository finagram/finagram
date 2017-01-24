package ru.finagram.test

import org.mockito.verification.VerificationWithTimeout
import org.mockito.{ ArgumentCaptor, Mockito }

import scala.concurrent.duration._
import scala.reflect.Manifest

trait MockitoSugar extends org.scalatest.mockito.MockitoSugar {

  def any[T <: Any](implicit manifest: Manifest[T]): T = {
    org.mockito.Matchers.any(manifest.runtimeClass.asInstanceOf[Class[T]])
  }

  def argumentCaptor[T <: Any](implicit manifest: Manifest[T]): ArgumentCaptor[T] = {
    ArgumentCaptor.forClass(manifest.runtimeClass.asInstanceOf[Class[T]])
  }

  def timeout(duration: Duration): VerificationWithTimeout = {
    Mockito.timeout(duration.toMillis.toInt)
  }
}
