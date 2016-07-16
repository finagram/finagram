package ru.finagram

import java.util.UUID

import org.mockito.Mockito._
import org.mockito.verification.VerificationWithTimeout
import org.mockito.{ ArgumentCaptor, Mockito }
import org.scalatest.words.ShouldVerb
import org.scalatest.{ FunSpecLike, Matchers }

import scala.concurrent.duration._
import scala.reflect.{ ClassTag, Manifest }

trait Spec extends FunSpecLike with Matchers with ShouldVerb {

  def mock[T <: AnyRef](implicit classTag: ClassTag[T]): T = {
    Mockito.mock(
      classTag.runtimeClass.asInstanceOf[Class[T]],
      withSettings.defaultAnswer(RETURNS_DEFAULTS)
    )
  }

  def any[T <: Any](implicit manifest: Manifest[T]): T = {
    org.mockito.Matchers.any(manifest.runtimeClass.asInstanceOf[Class[T]])
  }

  def argumentCaptor[T <: Any](implicit manifest: Manifest[T]): ArgumentCaptor[T] = {
    ArgumentCaptor.forClass(manifest.runtimeClass.asInstanceOf[Class[T]])
  }

  def timeout(duration: Duration): VerificationWithTimeout = {
    Mockito.timeout(duration.toMillis.toInt)
  }

  def randomString(): String = UUID.randomUUID().toString
}
