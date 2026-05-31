package br.com.dindin.domain.model

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "users")
class KomgaUser(
  @Email
  @NotBlank
  @Column(name = "email", nullable = false, unique = true)
  var email: String,

  @NotBlank
  @Column(name = "password", nullable = false)
  var password: String,

  @Enumerated(EnumType.STRING)
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_role", joinColumns = [JoinColumn(name = "user_id")])
  var roles: MutableSet<UserRoles> = mutableSetOf()

) : AuditableEntity() {

  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  var id: Long = 0

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name = "user_library_sharing",
    joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
    inverseJoinColumns = [JoinColumn(name = "library_id", referencedColumnName = "id")]
  )
  var sharedLibraries: MutableSet<Library> = mutableSetOf()

  @NotNull
  @Column(name = "shared_all_libraries", nullable = false)
  var sharedAllLibraries: Boolean = true
    get() = if (roles.contains(UserRoles.ADMIN)) true else field
    set(value) {
      field = if (roles.contains(UserRoles.ADMIN)) true else value
    }

  fun isAdmin() = roles.contains(UserRoles.ADMIN)

  fun canAccessBook(book: Book): Boolean {
    return sharedAllLibraries || sharedLibraries.any { it.id == book.series.library.id }
  }

  fun canAccessSeries(series: Series): Boolean {
    return sharedAllLibraries || sharedLibraries.any { it.id == series.library.id }
  }

  fun canAccessLibrary(library: Library): Boolean {
    return sharedAllLibraries || sharedLibraries.any { it.id == library.id }
  }
}

enum class UserRoles {
  ADMIN
}
