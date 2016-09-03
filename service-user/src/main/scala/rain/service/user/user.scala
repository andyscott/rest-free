/*
 * Rest Free
 */

package rain.service.user

import rain.patterns.service._

/** User operation algebra, with return type `A` */
sealed abstract class UserOp[A]
object UserOp {

  /** The algebra for [[UserService.create]] */
  case class Create(name: String) extends UserOp[User]

  /** The algebra for [[UserService.delete]] */
  case class Delete(id: UserId) extends UserOp[Boolean]

}

class UserService[IO[_]](lift: UserOp ~> IO) extends Service[IO] {

  /** Create a user with a given name.
    *
    * @return [[IO]] of the created [[User]]
    */
  def create(
    name: String
  ): IO[User] = lift(UserOp.Create(name))

  /** Delete a user with a given id.
    *
    * @return [[IO]] `Boolean` indicating success, or failure.
    */
  def delete(
    id: UserId
  ): IO[Boolean] = lift(UserOp.Delete(id))

}

/** @define Service UserService
  * @define Op UserOp
  */
object UserService extends ServiceCompanion[UserService, UserOp] {
  override def apply[IO[_]](trans: UserOp ~> IO): UserService[IO] = new UserService[IO](trans)
}
