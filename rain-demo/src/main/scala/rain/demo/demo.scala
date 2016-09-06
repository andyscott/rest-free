/*
 * Rest Free
 */

package rain.demo

import eu.timepit.refined.auto._
import eu.timepit.refined.numeric._

import cats.data.XorT

import akka.actor._
import akka.util.Timeout

import rain.common.akka._
import rain.service.user._
import rain.service.user.impl._

import scala.concurrent.duration._

object TestApp {

  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val timeout = Timeout(10.seconds)

  def main(args: Array[String]): Unit = {

    // example free program
    val userAPI = UserService.free
    val program = for {
      user ← XorT(userAPI.createUser(
        firstName = "Andy",
        lastName  = "Scott",
        age       = Some(28)))

      user2 ← XorT(userAPI.readUser(user.id))
    } yield (user, user2)

    // spin up service/backend
    val system = ActorSystem("default")
    val userServiceActor = system.actorOf(InMemoryUserServiceActor.props)

    // for Future
    {
      import scala.concurrent.Future
      import cats.instances.future._

      val userServiceInterp =
        AskFunctionK.instance[UserOp, Future](userServiceActor, timeout)

      // evaluate program
      val res = program.value.foldMap(userServiceInterp)
      res.foreach(r ⇒ println("Future result: " + r))
    }

    // for Task
    {
      import monix.cats._
      import monix.eval.Task
      import monix.execution.Scheduler

      val userServiceInterp =
        AskFunctionK.instance[UserOp, Task](userServiceActor, timeout)

      // evaluate program
      val res = program.value.foldMap(userServiceInterp)

      println("Task result: " + res.coeval(Scheduler.global).value)

    }

    Thread.sleep(100)
    system.terminate().foreach(_ ⇒ ())

  }

}
