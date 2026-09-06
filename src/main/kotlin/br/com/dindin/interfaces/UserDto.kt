package br.com.dindin.interfaces

import br.com.dindin.domain.model.KomgaUser
import br.com.dindin.infrastructure.security.KomgaPrincipal

data class UserDto(
    val id: Long,
    val email: String,
    val roles: Set<String>,
)

fun KomgaUser.toDto(): UserDto {
    val rolesSet: Set<String> = runCatching { roles.map { it.name }.toSet() }.getOrDefault(emptySet())
    return UserDto(
        id = id,
        email = email,
        roles = rolesSet + "USER"
    )
}

fun KomgaPrincipal.toDto() = user.toDto()
