package br.com.dindin.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime


@Entity
@Table(name = "authentication_activity")
data class AuthenticationActivity(

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    var id: Long = 0,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var userId: KomgaUser,
    val email: String? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "apikey_id", nullable = false)
    var apiKeyId: ApiKey,
    val apiKeyComment: String? = null,

    val ip: String? = null,
    val userAgent: String? = null,
    val success: Boolean,
    val error: String? = null,
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val source: String? = null,
)
