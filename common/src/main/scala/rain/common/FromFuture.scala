/*
 * Rest Free
 */

package rain.common

import cats._
import scala.concurrent.Future

/** A typeclass encoding the ability to create functor `F` from a
  * future. It's a natural transformation from `Future` to `F`.
  */
trait FromFuture[F[_]] extends (Future ~> F)
object FromFuture {
  def apply[F[_]](implicit ev: FromFuture[F]): FromFuture[F] = ev

  /** Create an instance from an underlying natural transformation */
  def apply[F[_]](trans: Future ~> F): FromFuture[F] =
    new FromFuture[F] {
      override def apply[A](futureA: Future[A]): F[A] = trans(futureA)
    }

  /** The identity instance, for `Future` itself. */
  implicit val id: FromFuture[Future] = apply(arrow.FunctionK.id[Future])
}
