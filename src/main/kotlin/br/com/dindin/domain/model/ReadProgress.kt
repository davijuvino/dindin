package br.com.dindin.domain.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@Entity
@Table(name = "read_progress")
open class ReadProgress(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long = 0,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: KomgaUser? = null,

    @NotNull
    @Column(name = "book_id", nullable = false)
    open var bookId: String = "",

    @NotNull
    @Column(nullable = false)
    open var completed: Boolean = false,

    @Column(name = "read_date", nullable = false, updatable = false)
    open var readDate: LocalDateTime = LocalDateTime.now(),

    open var deviceId: String = "",
    open var deviceName: String = "",
) : Auditable() {

    constructor() : this(
        id = 0,
        user = null,
        bookId = "",
        completed = false,
        readDate = LocalDateTime.now(),
        deviceId = "",
        deviceName = "",
    )

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is ReadProgress -> false
        id == 0L || other.id == 0L -> this === other
        else -> id == other.id
    }

    override fun hashCode(): Int = if (id == 0L) System.identityHashCode(this) else id.hashCode()
}
