/*
 * Rest Free
 */

package rain.patterns

import rain._

object service {

  type ~>[F[_], G[_]] = cats.arrow.FunctionK[F, G]
  type FunctionK[F[_], G[_]] = cats.arrow.FunctionK[F, G]
  val FunctionK = cats.arrow.FunctionK

  type Free[S[_], A] = cats.free.Free[S, A]
  val Free = cats.free.Free

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
  abstract class ServiceCompanion[Service[_[_]], Op[_]] {

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

}
