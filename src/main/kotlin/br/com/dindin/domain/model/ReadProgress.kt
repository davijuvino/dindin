package br.com.dindin.domain.model

import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import kotlin.jvm.Transient

@Entity
@Table(name = "read_progress")
@EntityListeners(AuditingEntityListener::class)
data class ReadProgress(

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
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

    @Column(nullable = false)
    var readDate: LocalDateTime = LocalDateTime.now(),

    var deviceId: String = "",

    var deviceName: String = "",

    @Transient
    var locator: R2Locator? = null,

    ) : Auditable()