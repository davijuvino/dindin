package br.com.dindin.domain.model

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.BatchSize

@Entity
@Table(name = "users")
data class KomgaUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0,

    @Email(regexp = ".+@.+\\..+")
    @NotBlank
    @Column(name = "email", nullable = false, unique = true)
    var email: String = "",

    @Column(name = "password")
    var password: String? = null,

    @ElementCollection(targetClass = UserRoles::class, fetch = FetchType.LAZY)
    @CollectionTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id")])
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @BatchSize(size = 20)
    var roles: Set<UserRoles> = emptySet(),

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_shared_libraries_ids", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "library_id")
    @BatchSize(size = 50)
    var sharedLibrariesIds: Set<String> = emptySet(),

    @NotNull
    @Column(name = "shared_all_libraries", nullable = false)
    var sharedAllLibraries: Boolean = false,

    @Embedded
    var restrictions: ContentRestrictions = ContentRestrictions(),

    @Column(name = "comment", nullable = true)
    var comment: String? = null,
) : Auditable() {

    constructor() : this(
        id = 0,
        email = "",
        password = "",
        roles = emptySet(),
        sharedLibrariesIds = emptySet(),
        sharedAllLibraries = false,
        restrictions = ContentRestrictions(),
        comment = null,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KomgaUser) return false
        if (id == 0L || other.id == 0L) return this === other
        return id == other.id
    }

    override fun hashCode(): Int = if (id == 0L) System.identityHashCode(this) else id.hashCode()

    override fun toString(): String = "KomgaUser(id=$id, email='$email', sharedAllLibraries=$sharedAllLibraries, createdDate=$createdDate, lastModifiedDate=$lastModifiedDate)"

    fun isAdmin(): Boolean = runCatching { roles.contains(UserRoles.ADMIN) }.getOrDefault(false)
    fun canAccessAllLibraries(): Boolean = sharedAllLibraries || isAdmin()
    fun canAccessLibraryById(libraryId: Long): Boolean = sharedAllLibraries || sharedLibrariesIds.contains(libraryId.toString()) || isAdmin()
    fun canAccessBookByLibraryId(libraryId: Long): Boolean = canAccessLibraryById(libraryId)
    fun canAccessSeriesByLibraryId(libraryId: Long): Boolean = canAccessLibraryById(libraryId)

    fun canAccessBook(book: Book): Boolean =
        try {
            val libId = book.series.library.id
            canAccessLibraryById(libId)
        } catch (_: Exception) {
            sharedAllLibraries || runCatching { sharedLibrariesIds.any { id -> id == book.series.library.id.toString() } }.getOrDefault(false)
        }

    fun canAccessSeries(series: Series): Boolean =
        try {
            val libId = series.library.id
            canAccessLibraryById(libId)
        } catch (_: Exception) {
            sharedAllLibraries || runCatching { sharedLibrariesIds.any { id -> id == series.library.id.toString() } }.getOrDefault(false)
        }

    fun canAccessLibrary(library: Library): Boolean = canAccessLibraryById(library.id)

    fun getAuthorizedLibraryIds(libraryIds: Collection<String>?): Collection<String>? =
        when {
            !canAccessAllLibraries() && libraryIds != null -> libraryIds.intersect(sharedLibrariesIds)
            !canAccessAllLibraries() && libraryIds == null -> sharedLibrariesIds
            libraryIds != null -> libraryIds
            else -> null
        }
}
