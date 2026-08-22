package br.com.dindin.domain.model

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name = "sync_point")
data class SyncPoint(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,

    @Column(name = "api_key_id")
    var apiKeyId: Long? = null,

    @Column(name = "created_date", nullable = false)
    var createdDate: ZonedDateTime = ZonedDateTime.now()
) {
    @OneToMany(mappedBy = "syncPoint", cascade = [CascadeType.ALL], orphanRemoval = true)
    var books: MutableList<SyncPointBook> = mutableListOf()

    @OneToMany(mappedBy = "syncPoint", cascade = [CascadeType.ALL], orphanRemoval = true)
    var readLists: MutableList<SyncPointReadList> = mutableListOf()
}
