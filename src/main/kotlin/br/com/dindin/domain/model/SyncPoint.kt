package br.com.dindin.domain.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.ZonedDateTime

@Entity
@Table(name = "sync_point")
data class SyncPoint(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: KomgaUser,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "api_key_id", nullable = true)
    var apiKey: ApiKey? = null,

    @Column(name = "created_date", nullable = false)
    var createdDate: ZonedDateTime = ZonedDateTime.now()
) {
    @OneToMany(mappedBy = "syncPoint", cascade = [CascadeType.ALL], orphanRemoval = true)
    var books: MutableList<SyncPointBook> = mutableListOf()

    @OneToMany(mappedBy = "syncPoint", cascade = [CascadeType.ALL], orphanRemoval = true)
    var readLists: MutableList<SyncPointReadList> = mutableListOf()
}
