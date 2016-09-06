/*
 * Rest Free
 */

package rain.service.user

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric._

case class User(
  id: User.Id,
  firstName: String,
  lastName: String,
  age: Option[User.Age]
)

object User {
  type Id = String
  type Age = Int Refined Positive
}
