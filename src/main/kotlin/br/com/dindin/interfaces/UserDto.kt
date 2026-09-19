package br.com.dindin.interfaces

import br.com.dindin.domain.model.KomgaUser
import br.com.dindin.infrastructure.security.KomgaPrincipal

data class UserDto(val id: Long, val email: String, val roles: Set<String>)
fun KomgaUser.toDto() = UserDto(id, email, roles.map { it.name }.toSet())
fun KomgaPrincipal.toDto() = user.toDto()
