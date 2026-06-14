package br.com.dindin.interfaces

import br.com.dindin.domain.model.KomgaUser
import br.com.dindin.infrastructure.security.KomgaPrincipal

data class UserDto(
    val id: Long,
    val email: String,
    val roles: Set<String>,
)

fun KomgaUser.toDto() =
    UserDto(
        id = id,
        email = email,
        roles = roles.map { it.name }.toSet() + "USER"
    )

fun KomgaPrincipal.toDto() = user.toDto()