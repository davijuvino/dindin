package br.com.dindin.domain.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.ZonedDateTime

@Entity
@Table(name = "sync_points")
open class SyncPoint(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: KomgaUser? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "api_key_id", nullable = true)
    var apiKey: ApiKey? = null,

    @Column(name = "created_date", nullable = false)
    var createdDate: ZonedDateTime = ZonedDateTime.now()
) : Auditable() {

    constructor() : this(id = 0, user = null, apiKey = null, createdDate = ZonedDateTime.now())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SyncPoint) return false
        if (id == 0L || other.id == 0L) return this === other
        return id == other.id
    }

    override fun hashCode(): Int = if (id == 0L) System.identityHashCode(this) else id.hashCode()
}
