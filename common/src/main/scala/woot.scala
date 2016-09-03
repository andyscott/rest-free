/*
 * Rest Free
 */

import language.experimental.macros
import language.existentials

import reflect.macros.blackbox.Context
import cats.arrow.FunctionK

package object rain {

  implicit class FunctionKEx(z: FunctionK.type) {
    def lift[F[_], G[_]](f: (F[α] ⇒ G[α]) forSome { type α }): FunctionK[F, G] = macro FunctionKExImpl.lift[F, G]
  }

  object FunctionKExImpl {

    // format: OFF
   def lift[
     F[_]: λ[α[_] ⇒ c.WeakTypeTag[α[_]]],
     G[_]: λ[α[_] ⇒ c.WeakTypeTag[α[_]]]
   ](c: Context)(
     f: c.Expr[F[α] ⇒ G[α]] forSome { type α }
    ): c.Expr[FunctionK[F, G]] = { // format: ON
      import c.universe._

      def unblock(tree: Tree): Tree = tree match {
        case Block(Nil, expr) ⇒ expr
        case _                ⇒ tree
      }

      def punchHole(tpe: Type): Tree = tpe match {
        case PolyType(undet :: Nil, underlying: TypeRef) ⇒
          val α = TypeName("α")
          def rebind(typeRef: TypeRef): Tree =
            if (typeRef.sym == undet) tq"$α"
            else {
              val args = typeRef.args.map {
                case ref: TypeRef ⇒ rebind(ref)
                case arg          ⇒ tq"$arg"
              }
              tq"${typeRef.sym}[..$args]"
            }
          val rebound = rebind(underlying)
          tq"""({type λ[$α] = $rebound})#λ"""
        case TypeRef(pre, sym, Nil) ⇒
          tq"$sym"
        case _ ⇒
          c.abort(c.enclosingPosition, s"Unexpected type $tpe when lifting to FunctionK")
      }

      val tree = unblock(f.tree) match {
        case q"""($param) => $trans[..$typeArgs](${ arg: Ident })""" if param.name == arg.name ⇒

          typeArgs
            .collect { case tt: TypeTree ⇒ tt }
            .find(_.original != null)
            .foreach { param ⇒
              c.abort(
                param.pos,
                s"type parameter $param must not be supplied when lifting function $trans to FunctionK")
            }

          val F = punchHole(weakTypeTag[F[_]].tpe)
          val G = punchHole(weakTypeTag[G[_]].tpe)

          q"""
          new FunctionK[$F, $G] {
            def apply[A](fa: $F[A]): $G[A] = $trans(fa)
          }
         """
        case other ⇒
          c.abort(other.pos, s"Unexpected tree $other when lifting to FunctionK")
      }

      c.Expr[FunctionK[F, G]](tree)
    }
  }

}
