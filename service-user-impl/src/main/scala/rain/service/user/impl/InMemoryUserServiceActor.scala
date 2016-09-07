/*
 * Rest Free
 */

package rain.service.user
package impl

import rain.patterns.service.akka._
import akka.actor._

import cats.data.Xor

class InMemoryUserServiceActor extends ServiceActor[UserOp] {
  import UserOp._

  val users = scala.collection.mutable.Map.empty[String, User]

  final override def process[A](rawOp: UserOp[A]) = rawOp match {
    case op: CreateUser ⇒ createUser(op)
    case op: ReadUser   ⇒ readUser(op)
    case op: UpdateUser ⇒ updateUser(op)
    case op: DeleteUser ⇒ deleteUser(op)
    case op: ListUsers  ⇒ listUsers(op)
  }

  private[this] def createUser(op: CreateUser): Res.Op[CreateUser] = {
    val user = User(
      id        = s"${System.currentTimeMillis}",
      moniker   = op.moniker,
      firstName = op.firstName,
      lastName  = op.lastName,
      age       = op.age
    )

    users += user.id → user

    Xor.right(user)
  }

  private[this] def readUser(op: ReadUser): Res.Op[ReadUser] =
    Xor.fromOption(
      users.get(op.id),
      DoesNotExist(op.id))

  private[this] def updateUser(op: UpdateUser): Res.Op[UpdateUser] =
    for {
      user ← Xor.fromOption(
        users.get(op.id),
        DoesNotExist(op.id))
      updatedUser = user.copy(
        firstName = op.updates.firstName getOrElse user.firstName,
        lastName  = op.updates.lastName getOrElse user.lastName,
        age       = op.updates.age getOrElse user.age)
      _ = users += user.id → updatedUser
    } yield updatedUser

  private[this] def deleteUser(op: DeleteUser): Res.Op[DeleteUser] =
    for {
      removedUser ← Xor.fromOption(
        users.get(op.id),
        DoesNotExist(op.id))
      _ = users -= op.id
    } yield removedUser

  private[this] def listUsers(op: ListUsers): Res.Op[ListUsers] =
    Xor.right(users.values.toList)

}

object InMemoryUserServiceActor {
  def props: Props = Props(classOf[InMemoryUserServiceActor])
}
