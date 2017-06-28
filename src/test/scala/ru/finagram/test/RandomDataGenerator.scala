package ru.finagram.test

import org.scalacheck.derive._
import org.scalacheck.rng.Seed
import org.scalacheck.{ Arbitrary, Gen }
import ru.finagram.api.User

import scala.util.Random

/**
 * Add method for generate random data every types.
 * <p/>
 * If you want to change a generation algorithm of the specified type,
 * you should determine a new implicit [[Arbitrary]] in the scope of the method random invocation.
 * For example, if you want to generate only alphabetical lowercase strings
 * then you should determine arbitrary for the type String:
 * {{{
 *  ...
 *  implicit lazy val arbString: Arbitrary[String] = Arbitrary(Gen.alphaLowerStr)
 *  ...
 *  random[String] // will generate a lower-case alpha character include empty string
 * }}}
 */
trait RandomDataGenerator extends SingletonInstances
  with HListInstances
  with CoproductInstances
  with DerivedInstances
  with FieldTypeInstances {

  implicit lazy val arbString: Arbitrary[String] =
    Arbitrary(Gen.alphaNumStr)

  /**
   * Generate random instance of T.
   *
   * @param arb generator for type T.
   * @param seed initial value for pseudo random values.
   * @tparam T type of the generated instance.
   * @return new instance of T with random field, or random value for simple types.
   */
  def random[T](implicit arb: Arbitrary[T], seed: Seed = Seed(Random.nextLong)): T = {
    val gen = Gen.listOfN(1, arb.arbitrary)
    val optSeqT = gen.apply(Gen.Parameters.default, seed)
    optSeqT.get.head
  }

  /**
   * Factory function for create random instance of T.
   */
  def arbitrary[T](f: => T): Arbitrary[T] = {
    Arbitrary(Gen.const(f))
  }
}
