/*
 * Rest Free
 */

package rain.patterns

package object service {

  /** Natural transformation from `F` to `G`. */
  type FunctionK[F[_], G[_]] = cats.arrow.FunctionK[F, G]
  val FunctionK = cats.arrow.FunctionK

  /** `~>` is an infix alias for [[FunctionK]] */
  type ~>[F[_], G[_]] = cats.arrow.FunctionK[F, G]
  val ~> = cats.arrow.FunctionK

  /** The Free data type */
  type Free[S[_], A] = cats.free.Free[S, A]
  val Free = cats.free.Free

  type Xor[A, B] = cats.data.Xor[A, B]

}
