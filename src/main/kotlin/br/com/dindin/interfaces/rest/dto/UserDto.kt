package br.com.dindin.interfaces.rest.dto

import br.com.dindin.domain.model.AgeRestriction
import br.com.dindin.domain.model.AllowExclude
import br.com.dindin.domain.model.KomgaUser
import br.com.dindin.infrastructure.security.KomgaPrincipal


data class UserDto(
  val id: String,
  val email: String,
  val roles: Set<String>,
  val sharedAllLibraries: Boolean,
  val sharedLibrariesIds: Set<String>,
  val labelsAllow: Set<String>,
  val labelsExclude: Set<String>,
  val ageRestriction: AgeRestrictionDto?,
)

data class AgeRestrictionDto(
    val age: Int,
    val restriction: AllowExclude,
)

fun AgeRestriction.toDto() = AgeRestrictionDto(age, restriction)

fun KomgaUser.toDto() =
  UserDto(
    id = id,
    email = email,
    roles = roles.map { it.name }.toSet() + "USER",
    sharedAllLibraries = sharedAllLibraries,
    sharedLibrariesIds = sharedLibrariesIds,
    labelsAllow = restrictions.labelsAllow,
    labelsExclude = restrictions.labelsExclude,
    ageRestriction = restrictions.ageRestriction?.toDto(),
  )

fun KomgaPrincipal.toDto() = user.toDto()
