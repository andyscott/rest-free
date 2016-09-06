/*
 * Rest Free
 */

package rain.service.user

import rain.patterns.service._
import rain.common.ClassTagA

import scala.reflect.ClassTag

class UserService[IO[_]](lift: UserOp ~> IO) extends Service[IO] {
  import UserOp._

  /** Create a user with a given name.
    *
    * @return [[IO]] of the created [[User]]
    */
  def createUser(
    firstName: String,
    lastName: String,
    age: Option[User.Age]
  ): IO[Error Xor User] = lift(CreateUser(firstName, lastName, age))

  def readUser(
    id: User.Id
  ): IO[Error Xor User] = lift(ReadUser(id))

  def listUsers(): IO[Error Xor List[User]] = lift(ListUsers())

  def updateUser(
    id: User.Id
  ): IO[Error Xor User] = lift(UpdateUser(id))

  /** Delete a user with a given id.
    *
    * @return [[IO]] `Boolean` indicating success, or failure.
    */
  def deleteUser(
    id: User.Id
  ): IO[Error Xor User] = lift(DeleteUser(id))

}

/** @define Service UserService
  * @define Op UserOp
  */
object UserService extends ServiceCompanion[UserService, UserOp] {
  override def apply[IO[_]](trans: UserOp ~> IO): UserService[IO] = new UserService[IO](trans)
}

/** User operation algebra, with return type `A` */
sealed abstract class UserOp[A: ClassTag] extends ClassTagA[A]
object UserOp {

  sealed abstract class Error extends Throwable {
    final override def fillInStackTrace = this
  }

  case class DoesNotExist(id: User.Id) extends Error

  case class CreateUser(
    firstName: String,
    lastName: String,
    age: Option[User.Age]
  ) extends UserOp[Error Xor User]

  case class ReadUser(
    id: User.Id
  ) extends UserOp[Error Xor User]

  case class UpdateUser(
    id: User.Id
  ) extends UserOp[Error Xor User]

  case class DeleteUser(
    id: User.Id
  ) extends UserOp[Error Xor User]

  case class ListUsers() extends UserOp[Error Xor List[User]]

}
