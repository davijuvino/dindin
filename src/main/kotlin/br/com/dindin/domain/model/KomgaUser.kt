package br.com.dindin.domain.model

import jakarta.persistence.*
import jakarta.persistence.Id
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
    val roles: Set<UserRoles> = mutableSetOf(),

    val sharedLibrariesIds: Set<String> = emptySet(),

    @NotNull
    @Column(name = "shared_all_libraries", nullable = false)
    var sharedAllLibraries: Boolean = false,

    val restrictions: ContentRestrictions = ContentRestrictions()

) : Auditable() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0

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

    var comment: String? = null

    /**
     * Default constructor for JPA.
     */
    constructor() : this(
        email = "",
        password = null,
        roles = emptySet()
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

}
