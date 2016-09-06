/*
 * Rest Free
 */

package rain.service.user

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric._

case class User(
  id: User.Id,
  moniker: String,
  firstName: String,
  lastName: String,
  age: Option[User.Age]
)

object User {
  type Id = String
  type Age = Int Refined Positive

  case class Update(
    firstName: Option[String] = None,
    lastName: Option[String] = None,
    age: Option[Option[User.Age]] = None
  )

}
