package br.com.dindin.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.NotBlank
import java.util.Locale


@Embeddable
class Author {
  constructor(name: String, role: String) {
    this.name = name
    this.role = role
  }

  @NotBlank
  @Column(name = "name", nullable = false)
  var name: String = ""
      set(value) {
      require(value.isNotBlank()) { "name must not be blank" }
      field = value.trim()
    }

  @NotBlank
  @Column(name = "role", nullable = false)
  var role: String = ""
      set(value) {
      require(value.isNotBlank()) { "role must not be blank" }
      field = value.trim().lowercase(Locale.getDefault())
    }

  override fun toString(): String = "Author($name, $role)"
}
