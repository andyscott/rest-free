/*
 * Rest Free
 */

package rain.patterns.service

import rain._

import cats.data.XorT

/** Base trait for services
  *
  * @tparam F type parameter for [[Service.IO]].
  */
trait Service[F[_]] {

  /** The container type, such as `Future` or `Free[Service, ?]`, that
    * service operation results are returned in
    */
  type IO[A] = F[A]
}

/** Base class for some common service companion methods.
  *
  * @define Service Service
  * @define Op Op
  */
abstract class ServiceCompanion[Service0[η[_]] <: Service[η], Op0[_]] {

  type Op[A] = Op0[A]
  type Service[A[_]] = Service0[A]

  /** Instantiates the `$Service` in given `IO` type.
    *
    * @param trans a transformation of `$Op` operations into `IO`
    */
  def apply[IO[_]](trans: Op ~> IO): Service[IO]

  /** The identity `$Service`, which return the underlying `$Op` algebra.
    * @usecase def id: Service[Op]
    * @inheritdoc
    */
  def id: Service[Op] = apply(FunctionK.id)

  /** Free algebra `$Service`
    *
    * @usecase def free: Service[({type λ[α] = Free[Op, α]})#λ]
    * @inheritdoc
    */
  def free: Service[Free[Op, ?]] = apply(FunctionK.lift(Free.liftF))

}

object ServiceCompanion {

  implicit class ExtrasService0[S[η[_]] <: Service[η], O[_]](val c: ServiceCompanion[S, O]) {
    type E = Throwable
    def foo: c.Service[XorT[Free[c.Op, ?], E, ?]] = ???

  }

}
