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
data class AuthenticationActivity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var userId: KomgaUser,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "apikey_id", nullable = false)
    var apiKeyId: ApiKey,

    @NotBlank
    @Column(name = "email", nullable = false)
    val email: String,

    @Column(name = "apiKeyComment", nullable = false)
    val apiKeyComment: String,

    val ip: String? = null,
    val userAgent: String? = null,
    val success: Boolean = false,
    val error: String? = null,

    @Column(name = "date_time", nullable = false)
    val dateTime: LocalDateTime = LocalDateTime.now(),

    val source: String? = null,
) {
    /**
     * Default constructor for JPA.
     */
    constructor() : this(
        id = 0,
        userId = KomgaUser(),
        apiKeyId = ApiKey(userId = KomgaUser(), pkey = "", comment = ""),
        email = "",
        apiKeyComment = "",
        ip = null,
        userAgent = null,
        success = false,
        error = null,
        dateTime = LocalDateTime.now(),
        source = null
    )
}
