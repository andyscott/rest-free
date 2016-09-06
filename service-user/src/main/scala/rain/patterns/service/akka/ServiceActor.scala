/*
 * Rest Free
 */

package rain.patterns.service.akka

import akka.actor._
import akka.pattern.pipe

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.reflect.ClassTag

abstract class ServiceActor[Op[_]](implicit evOp: ClassTag[Op[_]]) extends Actor {

  protected[this]type Res[A] = ServiceActor.Res[A]
  protected[this] val Res = ServiceActor.Res

  final override def receive = {
    case evOp(op) ⇒
      process(op).send(sender)
      ()
  }

  protected def process[A](op: Op[A]): Res[A]
}

object ServiceActor {

  trait Res[A] {
    def send(ref: ActorRef): Unit
  }

  object Res {
    type Op[O <: { type A }] = Res[O#A]

    implicit def fromConstant[A](constant: A): Res[A] = new Res[A] {
      def send(ref: ActorRef): Unit = {
        ref ! constant
      }
    }

    implicit def fromFuture[A](future: Future[A])(implicit ec: ExecutionContext): Res[A] = new Res[A] {
      def send(ref: ActorRef): Unit = {
        pipe(future)(ec).to(ref)
        ()
      }
    }
  }

}
