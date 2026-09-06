package br.com.dindin.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@Entity
@Table(name = "authentication_activity")
open class AuthenticationActivity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: KomgaUser? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "apikey_id", nullable = false)
    var apiKey: ApiKey? = null,

    @NotBlank
    @Column(name = "email", nullable = false)
    var email: String = "",

    @Column(name = "apiKeyComment", nullable = false)
    var apiKeyComment: String = "",

    var ip: String? = null,
    var userAgent: String? = null,
    var success: Boolean = false,
    var error: String? = null,

    @Column(name = "date_time", nullable = false)
    var dateTime: LocalDateTime = LocalDateTime.now(),

    var source: String? = null
) : Auditable() {

    constructor() : this(
        id = 0,
        user = null,
        apiKey = null,
        email = "",
        apiKeyComment = "",
        ip = null,
        userAgent = null,
        success = false,
        error = null,
        dateTime = LocalDateTime.now(),
        source = null
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AuthenticationActivity) return false
        if (id == 0L || other.id == 0L) return this === other
        return id == other.id
    }

    override fun hashCode(): Int = if (id == 0L) System.identityHashCode(this) else id.hashCode()

}
