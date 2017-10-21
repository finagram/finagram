package ru.finagram.test

import org.hamcrest.{ BaseMatcher, Description }
import org.mockito.Matchers.argThat
import org.mockito.Mockito.{ doAnswer, mock => mockitoMock }
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.mockito.{ ArgumentCaptor, MockSettings, Mockito }
import org.mockito.verification.VerificationWithTimeout

import scala.concurrent.duration._
import scala.reflect.Manifest

trait MockitoSugar {

  /**
   * Invokes the <code>mock(classToMock: Class[T])</code> method on the <code>Mockito</code>
   * companion object (<em>i.e.</em>, the static <code>mock(java.lang.Class<T> classToMock)</code>
   * method in Java class <code>org.mockito.Mockito</code>).
   *
   * <p>
   * Using the Mockito API directly, you create a mock with:
   * </p>
   *
   * <pre class="stHighlight">
   * val mockCollaborator = mock(classOf[Collaborator])
   * </pre>
   *
   * <p>
   * Using this method, you can shorten that to:
   * </p>
   *
   * <pre class="stHighlight">
   * val mockCollaborator = mock[Collaborator]
   * </pre>
   */
  def mock[T <: AnyRef : Manifest]: T = {
    mockitoMock(manifest[T].runtimeClass.asInstanceOf[Class[T]])
  }

  /**
   * Invokes the <code>mock(classToMock: Class[T], defaultAnswer: Answer[_])</code>
   * method on the <code>Mockito</code> companion object (<em>i.e.</em>, the static
   * <code>mock(java.lang.Class<T> classToMock, org.mockito.stubbing.Answer defaultAnswer)</code>
   * method in Java class <code>org.mockito.Mockito</code>).
   *
   * <p>
   * Using the Mockito API directly, you create a mock with:
   * </p>
   *
   * <pre class="stHighlight">
   * val mockCollaborator = mock(classOf[Collaborator], defaultAnswer)
   * </pre>
   *
   * <p>
   * Using this method, you can shorten that to:
   * </p>
   *
   * <pre class="stHighlight">
   * val mockCollaborator = mock[Collaborator](defaultAnswer)
   * </pre>
   */
  def mock[T <: AnyRef : Manifest](defaultAnswer: Answer[_]): T = {
    mockitoMock(manifest[T].runtimeClass.asInstanceOf[Class[T]], defaultAnswer)
  }

  /**
   * Invokes the <code>mock(classToMock: Class[T], mockSettings: MockSettings)</code> method on the
   * <code>Mockito</code> companion object (<em>i.e.</em>, the static
   * <code>mock(java.lang.Class<T> classToMock, org.mockito.MockSettings mockSettings)</code>
   * method in Java class <code>org.mockito.Mockito</code>).
   *
   * <p>
   * Using the Mockito API directly, you create a mock with:
   * </p>
   *
   * <pre class="stHighlight">
   * val mockCollaborator = mock(classOf[Collaborator], mockSettings)
   * </pre>
   *
   * <p>
   * Using this method, you can shorten that to:
   * </p>
   *
   * <pre class="stHighlight">
   * val mockCollaborator = mock[Collaborator](mockSettings)
   * </pre>
   */
  def mock[T <: AnyRef : Manifest](mockSettings: MockSettings): T = {
    mockitoMock(manifest[T].runtimeClass.asInstanceOf[Class[T]], mockSettings)
  }

  /**
   * Invokes the <code>mock(classToMock: Class[T], name: String)</code> method on the
   * <code>Mockito</code> companion object (<em>i.e.</em>,
   * the static <code>mock(java.lang.Class<T> classToMock, java.lang.String name)</code>
   * method in Java class <code>org.mockito.Mockito</code>).
   *
   * <p>
   * Using the Mockito API directly, you create a mock with:
   * </p>
   *
   * <pre class="stHighlight">
   * val mockCollaborator = mock(classOf[Collaborator], name)
   * </pre>
   *
   * <p>
   * Using this method, you can shorten that to:
   * </p>
   *
   * <pre class="stHighlight">
   * val mockCollaborator = mock[Collaborator](name)
   * </pre>
   */
  def mock[T <: AnyRef : Manifest](name: String): T = {
    mockitoMock(manifest[T].runtimeClass.asInstanceOf[Class[T]], name)
  }

  /**
   * Invokes the <code>any(classToMatch: Class[T])</code> method.
   *
   * <p>
   * Using the Mockito API directly, you create a matcher with:
   * </p>
   *
   * <pre class="stHighlight">
   * any(classOf[MyClass])
   * </pre>
   *
   * <p>
   * Using this method, you can shorten that to:
   * </p>
   *
   * <pre class="stHighlight">
   * any[MyClass]
   * </pre>
   */
  // TODO remove when pull request https://github.com/scalatest/scalatest/pull/546 will accepted
  def any[T <: Any]: T = {
    org.mockito.Matchers.any(manifest.runtimeClass.asInstanceOf[Class[T]])
  }

  /**
   * Invokes the <code>ArgumentCaptor.forClass(classToCapture: Class[T])</code> method.
   *
   * <p>
   * Using the Mockito API directly, you create a matcher with:
   * </p>
   *
   * <pre class="stHighlight">
   * ArgumentCaptor.forClass(classOf[MyClass])
   * </pre>
   *
   * <p>
   * Using this method, you can shorten that to:
   * </p>
   *
   * <pre class="stHighlight">
   * argumentCaptor[MyClass]
   * </pre>
   */
  // TODO remove when pull request https://github.com/scalatest/scalatest/pull/867 will accepted
  def argumentCaptor[T <: Any]: ArgumentCaptor[T] = {
    ArgumentCaptor.forClass(manifest.runtimeClass.asInstanceOf[Class[T]])
  }

  /**
   * Invokes the <code>Mockito.timeout(millis: Int)</code> method.
   *
   * <p>
   * Using the Mockito API directly, you create a matcher with:
   * </p>
   *
   * <pre class="stHighlight">
   * timeout(3000)
   * </pre>
   *
   * <p>
   * Using this method, you can write more clearly:
   * </p>
   *
   * <pre class="stHighlight">
   * import scala.concurrent.duration._
   * timeout(3.seconds)
   * </pre>
   */
  def timeout(duration: Duration): VerificationWithTimeout = {
    Mockito.timeout(duration.toMillis.toInt)
  }

  /**
   * Use `doLazyReturn()` when you want to stub a method that should return lazy
   * computed result.
   *
   * Example:
   *
   * {{{
   *   def f: T = { ... }
   *   doLazyReturn(f).when(mock).someMethod();
   * }}}
   *
   * @param f function which will invoked to compute result of stub's method invocation
   *
   * @return stubber - to select a method for stubbing
   */
  def doLazyReturn[T](f: => T) = doAnswer(
    new Answer[T] {
      override def answer(invocation: InvocationOnMock) = f
    }
  )

  /**
   * Add matcher for argument with lazy comparision.
   * Use it to mix [[org.mockito.Matchers#any(java.lang.Class)]] with
   * real expected values of arguments.
   *
   * Example:
   *
   * {{{
   *   lazy val expectedArg = ...
   *   doReturn(...).when(stub).someMethodWithArgs(arg(expectedArg), any())
   * }}}
   * @param f function which will invoked to get value for comparision with method invocation argument.
   * @tparam T
   * @return
   */
  def arg[T](f: => T) = argThat(
    new BaseMatcher[T] {
      override def matches(item: scala.Any) = f == item

      override def describeTo(description: Description) = {}
    }
  )
}
