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

@Entity
@Table(name = "api_key")
data class ApiKey(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0,
    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: KomgaUser? = null,
    @NotBlank @Column(name = "pkey", nullable = false, unique = true)
    var pkey: String = "",
    @NotBlank @Column(name = "comment", nullable = false)
    var comment: String = "",
) : Auditable() {
    constructor() : this(id = 0, user = null, pkey = "", comment = "")

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is ApiKey -> false
        id == 0L || other.id == 0L -> false
        else -> id == other.id
    }
    override fun hashCode(): Int = if (id == 0L) System.identityHashCode(this) else id.hashCode()
    override fun toString(): String = "ApiKey(id=$id, pkey='${pkey.take(8)}...', comment='$comment')"
}
