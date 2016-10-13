package ru.finagram.test

import org.mockito.Mockito._
import org.mockito.verification.VerificationWithTimeout
import org.mockito.{ ArgumentCaptor, Mockito }
import uk.co.jemos.podam.api.PodamFactoryImpl

import scala.concurrent.duration._
import scala.reflect.{ ClassTag, Manifest }

trait Utils {

  protected val factory = new PodamFactoryImpl()

  def random[T <: AnyRef](implicit classTag: ClassTag[T]): T = {
    factory.manufacturePojoWithFullData(
      classTag.runtimeClass.asInstanceOf[Class[T]]
    )
  }

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
}
