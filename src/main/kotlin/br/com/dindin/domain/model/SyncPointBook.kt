package br.com.dindin.domain.model

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name = "sync_point_book")
class SyncPointBook(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sync_point_id", nullable = false)
    var syncPoint: SyncPoint? = null,

    @Column(name = "book_id", nullable = false)
    var bookId: Long = 0,

    @Column(name = "created_date", nullable = false)
    var createdDate: ZonedDateTime = ZonedDateTime.now(),

    @Column(name = "last_modified_date", nullable = false)
    var lastModifiedDate: ZonedDateTime = ZonedDateTime.now(),

    @Column(name = "file_last_modified", nullable = false)
    var fileLastModified: ZonedDateTime = ZonedDateTime.now(),

    @Column(name = "file_size", nullable = false)
    var fileSize: Long = 0,

    @Column(name = "file_hash", nullable = false)
    var fileHash: String = "",

    @Column(name = "metadata_last_modified_date", nullable = false)
    var metadataLastModifiedDate: ZonedDateTime = ZonedDateTime.now(),

    @Column(name = "thumbnail_id")
    var thumbnailId: String? = null,

    @Column(name = "synced", nullable = false)
    var synced: Boolean = false
)