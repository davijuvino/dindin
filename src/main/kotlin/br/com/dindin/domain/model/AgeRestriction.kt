package br.com.dindin.domain.model

data class AgeRestriction(
  val age: Int,
  val restriction: AllowExclude,
)

enum class AllowExclude {
  ALLOW_ONLY,
  EXCLUDE,
}
