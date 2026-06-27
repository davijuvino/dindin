package br.com.dindin.interfaces

import jakarta.validation.constraints.NotBlank

data class ApiKeyRequestDto(
  @get:NotBlank
  val comment: String,
)
