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
import java.time.ZoneId
import java.time.ZonedDateTime

@Entity
@Table(name = "api_key")
data class ApiKey(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var userId: KomgaUser? = null,

    @NotBlank
    @Column(name = "pkey", nullable = false, unique = true)
    var pkey: String = "",

    @NotBlank
    @Column(name = "comment", nullable = false)
    var comment: String = "",
) : Auditable() {

    // JPA-friendly default constructor
    constructor() : this(id = 0, userId = null, pkey = "", comment = "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ApiKey) return false
        if (id == 0L || other.id == 0L) return this === other
        return id == other.id
    }

    override fun hashCode(): Int = if (id == 0L) System.identityHashCode(this) else id.hashCode()

    override fun toString(): String = "ApiKey(id=$id, pkey='${pkey.take(8)}...', comment='$comment')"
}
