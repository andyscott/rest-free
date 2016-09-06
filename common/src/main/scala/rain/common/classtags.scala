/*
 * Rest Free
 */

package rain.common

import scala.reflect.ClassTag

/** Type class encoding the ability to get the class tag for a single
  * hole.
  *
  * @author Andy Scott [47 Degrees]
  */
trait HoleClassTag[F[_]] {
  def classTagA[A](fa: F[A]): ClassTag[A]
}

/** Helpers for [[HoleClassTag]].
  *
  * @author Andy Scott [47 Degrees]
  */
object HoleClassTag {
  def apply[F[_]: HoleClassTag](implicit ev: HoleClassTag[F]): HoleClassTag[F] = ev

  implicit def mkInstance[A, F[A] <: ClassTagA[A]]: HoleClassTag[F] =
    new HoleClassTag[F] {
      override def classTagA[A0](fa: F[A0]): ClassTag[A0] = fa.classTagA
    }
}

/** Base class for types that wish to capture a classtag for
  * a single type parameter (named `A`).
  *
  * @author Andy Scott [47 Degrees]
  */
abstract class ClassTagA[A0: ClassTag] {
  type A = A0
  final val classTagA: ClassTag[A] = implicitly[ClassTag[A]]
}
