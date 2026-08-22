package br.com.dindin.domain.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import net.greypanther.natsort.CaseInsensitiveSimpleNaturalComparator
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.net.URL
import java.time.LocalDateTime
import java.util.*

private val natSortComparator: Comparator<String> = CaseInsensitiveSimpleNaturalComparator.getInstance()

@Entity
@Table(name = "series")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "cache.series")
class Series(
    @NotBlank
    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "url", nullable = false)
    var url: URL,

    @Column(name = "file_last_modified", nullable = false)
    var fileLastModified: LocalDateTime,

    books: Iterable<Book>

) : Auditable() {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    var id: Long = 0

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "library_id", nullable = false)
    lateinit var library: Library

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "series")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "cache.series.collection.books")
    private var _books: MutableList<Book> = mutableListOf()

    var books: List<Book>
        get() = _books.toList()
        set(value) {
            _books.clear()
            value.forEach { it.series = this }
            _books.addAll(value.sortedWith(compareBy(natSortComparator) { it.name }))
            _books.forEachIndexed { index, book -> book.number = index + 1 }
        }

    @OneToOne(optional = false, orphanRemoval = true, cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "metadata_id", nullable = false)
    var metadata: SeriesMetadata = SeriesMetadata(title = name)

    init {
        this.books = books.toList()
    }

    override fun toString(): String = "Series($id, ${url.toURI().path})"
}