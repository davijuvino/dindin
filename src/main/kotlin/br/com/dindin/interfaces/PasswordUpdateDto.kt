package br.com.dindin.interfaces

import jakarta.validation.constraints.NotBlank

data class PasswordUpdateDto(
    @get:NotBlank val password: String,
)
