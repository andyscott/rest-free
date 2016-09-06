/*
 * Rest Free
 */

package rain.common.akka

import cats.~>
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import rain.common.FromFuture
import rain.common.HoleClassTag

/** Companion for [[AskFunctionK]].
  *
  * @author Andy Scott [47 Degrees]
  */
object AskFunctionK {
  def instance[F[_]: HoleClassTag, G[_]: FromFuture](peer: ActorRef, timeout: Timeout): AskFunctionK[F, G] =
    new AskFunctionK(peer, timeout)
}

/** A natural transformation from `F` to `G`, backed by asking `peer` to handle
  * the entire value of `F[A]`.
  *
  * @author Andy Scott [47 Degrees]
  */
class AskFunctionK[F[_], G[_]](peer: ActorRef, timeout: Timeout)(
    implicit
    hole: HoleClassTag[F],
    G: FromFuture[G]
) extends (F ~> G) {

  override def apply[A](fa: F[A]): G[A] =
    G(peer.ask(fa)(timeout).mapTo[A](hole.classTagA(fa)))

}
