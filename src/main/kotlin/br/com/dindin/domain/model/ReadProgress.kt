package br.com.dindin.domain.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import jakarta.persistence.*
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
    var userId: KomgaUser? = null,

    @NotNull
    @Column(name = "book_id", nullable = false)
    var bookId: String = "",

    @NotNull
    @Column(nullable = false)
    var completed: Boolean = false,

    @CreatedDate
    @Column(name = "read_date", nullable = false, updatable = false)
    var readDate: LocalDateTime = LocalDateTime.now(),

    var deviceId: String = "",

    var deviceName: String = "",

) : Auditable() {

    constructor() : this(id = 0, userId = null, bookId = "", completed = false, readDate = LocalDateTime.now(), deviceId = "", deviceName = "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReadProgress) return false
        if (id == 0L || other.id == 0L) return this === other
        return id == other.id
    }

    override fun hashCode(): Int = if (id == 0L) System.identityHashCode(this) else id.hashCode()
}
