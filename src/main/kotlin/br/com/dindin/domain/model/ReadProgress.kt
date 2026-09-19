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
@Table(name = "read_progress")
open class ReadProgress(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: KomgaUser? = null,

    @NotNull
    @Column(name = "book_id", nullable = false)
    var bookId: String = "",

    @NotNull
    @Column(nullable = false)
    var completed: Boolean = false,

    @Column(name = "read_date", nullable = false, updatable = false)
    var readDate: java.time.LocalDateTime = java.time.LocalDateTime.now(),

    var deviceId: String = "",
    var deviceName: String = "",
) : Auditable() {

    constructor() : this(
        id = 0,
        user = null,
        bookId = "",
        completed = false,
        readDate = java.time.LocalDateTime.now(),
        deviceId = "",
        deviceName = ""
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReadProgress) return false
        if (id == 0L || other.id == 0L) return this === other
        return id == other.id
    }

    override fun hashCode(): Int = if (id == 0L) System.identityHashCode(this) else id.hashCode()
}
