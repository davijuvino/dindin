package br.com.dindin.domain.model

import jakarta.persistence.*
import jakarta.persistence.Id
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "users")
data class KomgaUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0,

    @Email(regexp = ".+@.+\\..+")
    @NotBlank
    @Column(name = "email", nullable = false, unique = true)
    val email: String,

    @NotBlank
    @Column(name = "password", nullable = false)
    var password: String,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    val roles: Set<UserRoles> = mutableSetOf(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_shared_libraries_ids", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "library_id")
    val sharedLibrariesIds: Set<String> = emptySet(),

    @NotNull
    @Column(name = "shared_all_libraries", nullable = false)
    var sharedAllLibraries: Boolean = false,

    @Embedded
    val restrictions: ContentRestrictions = ContentRestrictions(),

    @Column(name = "comment", nullable = true)
    var comment: String? = null

) : Auditable() {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_library_sharing",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "library_id", referencedColumnName = "id")]
    )
    val sharedLibraries: MutableSet<Library> = mutableSetOf()

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], optional = true)
    @JoinColumn(name = "api_key_id", referencedColumnName = "id")
    var apiKey: ApiKey? = null

    /**
     * Default constructor for JPA.
     */
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

    fun isAdmin() = roles.contains(UserRoles.ADMIN)

    fun canAccessAllLibraries(): Boolean = sharedAllLibraries || isAdmin()

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
