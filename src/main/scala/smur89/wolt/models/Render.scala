package smur89.wolt.models

trait Render[A] {
  def render(a: A): String
}
