package br.com.dindin.domain.model

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.BatchSize

@Entity
@Table(name = "users")
open class KomgaUser(
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
    var comment: String? = null
) : Auditable() {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_key_id", referencedColumnName = "id")
    var apiKey: ApiKey? = null

    constructor() : this(
        id = 0,
        email = "",
        password = "",
        roles = emptySet(),
        sharedLibrariesIds = emptySet(),
        sharedAllLibraries = false,
        restrictions = ContentRestrictions(),
        comment = null
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KomgaUser) return false
        if (id == 0L || other.id == 0L) return this === other
        return id == other.id
    }

    override fun hashCode(): Int = if (id == 0L) System.identityHashCode(this) else id.hashCode()

    // Avoid forcing LAZY collections in logs
    override fun toString(): String = "KomgaUser(id=$id, email='$email', sharedAllLibraries=$sharedAllLibraries, createdDate=$createdDate, lastModifiedDate=$lastModifiedDate)"

    fun isAdmin(): Boolean = runCatching { roles.contains(UserRoles.ADMIN) }.getOrDefault(false)

    fun canAccessAllLibraries(): Boolean = sharedAllLibraries || isAdmin()

    // Prefer checking by stored library ids (sharedLibrariesIds) to avoid initializing LAZY collections
    fun canAccessLibraryById(libraryId: Long): Boolean = sharedAllLibraries || sharedLibrariesIds.contains(libraryId.toString()) || isAdmin()

    fun canAccessBookByLibraryId(libraryId: Long): Boolean = canAccessLibraryById(libraryId)

    fun canAccessSeriesByLibraryId(libraryId: Long): Boolean = canAccessLibraryById(libraryId)

    // Backwards-compatible helpers that attempt safe checks without forcing heavy loads
    fun canAccessBook(book: Book): Boolean {
        // try to resolve library id in a safe way, prefer using Series/Library ids if available
        return try {
            val libId = book.series.library.id
            canAccessLibraryById(libId)
        } catch (_: Exception) {
            // fallback to checking sharedLibraries collection but don't throw
            sharedAllLibraries || runCatching { sharedLibraries.any { it.id == book.series.library.id } }.getOrDefault(false)
        }
    }

    fun canAccessSeries(series: Series): Boolean {
        return try {
            val libId = series.library.id
            canAccessLibraryById(libId)
        } catch (_: Exception) {
            sharedAllLibraries || runCatching { sharedLibraries.any { it.id == series.library.id } }.getOrDefault(false)
        }
    }

    fun canAccessLibrary(library: Library): Boolean {
        return canAccessLibraryById(library.id)
    }

    /**
     * Return the list of LibraryIds this user is authorized to view, intersecting the provided list of LibraryIds.
     * @param libraryIds an optional list of LibraryIds to filter on
     * @return a list of authorised LibraryIds, or null if the user is authorised to see all libraries
     */
    fun getAuthorizedLibraryIds(libraryIds: Collection<String>?): Collection<String>? =
        when {
            // limited user & libraryIds are specified: filter on provided libraries intersecting user's authorized libraries
            !canAccessAllLibraries() && libraryIds != null -> libraryIds.intersect(sharedLibrariesIds)

            // limited user: filter on user's authorized libraries
            !canAccessAllLibraries() && libraryIds == null -> sharedLibrariesIds

            // non-limited user & libraryIds are specified: filter on provided libraries
            libraryIds != null -> libraryIds

            // non-limited user & no libraryIds specified: return null, meaning no filtering
            else -> null
        }
}
