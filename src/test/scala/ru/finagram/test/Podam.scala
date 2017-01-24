package ru.finagram.test

import uk.co.jemos.podam.api.PodamFactoryImpl

import scala.reflect.ClassTag

trait Podam {
  protected val factory = new PodamFactoryImpl()

  def random[T <: AnyRef](implicit classTag: ClassTag[T]): T = {
    factory.manufacturePojoWithFullData(
      classTag.runtimeClass.asInstanceOf[Class[T]]
    )
  }
}
