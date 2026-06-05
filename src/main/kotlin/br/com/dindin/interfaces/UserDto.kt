package br.com.dindin.interfaces

import br.com.dindin.domain.model.KomgaUser

data class UserDto(
    val id: Long,
    val email: String,
    val roles: List<String>
)

fun KomgaUser.toDto() =
    UserDto(
        id = id,
        email = email,
        roles = roles.map { it.name }
    )