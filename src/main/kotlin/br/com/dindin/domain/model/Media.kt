package br.com.dindin.domain.model

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import jakarta.persistence.*

@Entity
@Table(name = "media")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "cache.media")
class Media(
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: Status = Status.UNKNOWN,

    @Column(name = "media_type")
    var mediaType: String? = null,

    @Column(name = "thumbnail")
    @Lob
    var thumbnail: ByteArray? = null,

    pages: Iterable<BookPage> = emptyList(),

    files: Iterable<String> = emptyList(),

    @Column(name = "comment")
    var comment: String? = null
) : Auditable() {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    val id: Long = 0

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "media_page", joinColumns = [JoinColumn(name = "media_id")])
    @OrderColumn(name = "number")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "cache.media.collection.pages")
    private var _pages: MutableList<BookPage> = mutableListOf()

    var pages: List<BookPage>
        get() = _pages.toList()
        set(value) {
            _pages.clear()
            _pages.addAll(value)
        }

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "media_file", joinColumns = [JoinColumn(name = "media_id")])
    @Column(name = "files")
    private var _files: MutableList<String> = mutableListOf()

    var files: List<String>
        get() = _files.toList()
        set(value) {
            _files.clear()
            _files.addAll(value)
        }

    fun reset() {
        status = Status.UNKNOWN
        mediaType = null
        thumbnail = null
        comment = null
        _pages.clear()
        _files.clear()
    }

    init {
        this.pages = pages.toList()
        this.files = files.toList()
    }

    enum class Status {
        UNKNOWN, ERROR, READY, UNSUPPORTED
    }

}