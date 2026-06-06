package br.com.dindin.domain.model

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "users")
data class KomgaUser(
    @Email(regexp = ".+@.+\\..+")
    @NotBlank
    @Column(name = "email", nullable = false, unique = true)
    val email: String,

    @NotBlank
    @Column(name = "password", nullable = false)
    var password: String?,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    val roles: Set<UserRoles> = mutableSetOf()
) : Auditable() {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    val id: Long = 0

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_library_sharing",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "library_id", referencedColumnName = "id")]
    )
    val sharedLibraries: MutableSet<Library> = mutableSetOf()

    @NotNull
    @Column(name = "shared_all_libraries", nullable = false)
    var sharedAllLibraries: Boolean = false
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

    override fun toString(): String = "KomgaUser(id=$id, email='$email', roles=$roles, sharedAllLibraries=$sharedAllLibraries, createdDate=$createdDate, lastModifiedDate=$lastModifiedDate)"
}
