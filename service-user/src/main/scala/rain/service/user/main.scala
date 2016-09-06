/*
 * Rest Free
 */

package rain.service.user

import eu.timepit.refined.auto._
import eu.timepit.refined.numeric._

import cats.data.XorT

object TestApp {

  def main(args: Array[String]): Unit = {

    val userService = UserService.free

    val program = for {
      user ← XorT(userService.createUser(
        firstName = "Andy",
        lastName  = "Scott",
        age       = Some(28)))

      user2 ← XorT(userService.readUser(user.id))
    } yield (user.id == user2.id)

  }

}
