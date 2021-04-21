package smur89.wolt.models

package object syntax {

  object Render {
    implicit class RenderOps[A](value: A) {
      def render(implicit R: Render[A]): String = R.render(value)
    }
  }

}
