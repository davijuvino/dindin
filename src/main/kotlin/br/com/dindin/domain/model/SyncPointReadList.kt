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
import java.time.ZonedDateTime

@Entity
@Table(name = "sync_point_read_list")
class SyncPointReadList(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sync_point_id", nullable = false)
    var syncPoint: SyncPoint? = null,

    @Column(name = "read_list_id", nullable = false)
    var readListId: Long = 0,

    @Column(name = "read_list_name", nullable = false)
    var readListName: String = "",

    @Column(name = "created_date", nullable = false)
    var createdDate: ZonedDateTime = ZonedDateTime.now(),

    @Column(name = "last_modified_date", nullable = false)
    var lastModifiedDate: ZonedDateTime = ZonedDateTime.now(),

    @Column(name = "synced", nullable = false)
    var synced: Boolean = false
)