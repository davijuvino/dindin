package br.com.dindin.domain.model

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

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

    @Enumerated(EnumType.STRING)
    @Column(name = "roles")
    var roles: Set<UserRoles> = mutableSetOf(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_shared_libraries_ids", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "library_id")
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

    override fun toString(): String = "KomgaUser(id=$id, email='$email', roles=$roles, sharedAllLibraries=$sharedAllLibraries, createdDate=$createdDate, lastModifiedDate=$lastModifiedDate)"
}
