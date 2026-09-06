package br.com.dindin.domain.model

import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "read_progress")
@EntityListeners(AuditingEntityListener::class)
data class ReadProgress(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    var bookId: Book,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var userId: KomgaUser,

    @Column(nullable = false)
    var page: Int,

    @Column(nullable = false)
    var completed: Boolean = false,

    @CreatedDate
    @Column(name = "read_date", nullable = false, updatable = false)
    var readDate: LocalDateTime = LocalDateTime.now(),

    var deviceId: String = "",

    var deviceName: String = "",

    ) : Auditable()
