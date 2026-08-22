package br.com.dindin.domain.model

import com.jakewharton.byteunits.BinaryByteUnit
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.apache.commons.io.FilenameUtils
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime


@Entity
@Table(name = "book")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "cache.book")
class Book(
    @NotBlank
    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "url", nullable = false)
    var url: URL,

    @Column(name = "file_last_modified", nullable = false)
    var fileLastModified: LocalDateTime,

    @Column(name = "file_size", nullable = false)
    var fileSize: Long = 0
) : Auditable() {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    var id: Long = 0

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "series_id", nullable = false)
    lateinit var series: Series

    @OneToOne(optional = false, orphanRemoval = true, cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", nullable = false)
    var media: Media = Media()

    @Column(name = "number", nullable = false)
    var number: Int = 0
        set(value) {
            field = value
            if (!metadata.numberLock) metadata.number = value.toString()
            if (!metadata.numberSortLock) metadata.numberSort = value.toFloat()
        }

    @OneToOne(optional = false, orphanRemoval = true, cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "metadata_id", nullable = false)
    var metadata: BookMetadata =
        BookMetadata(
            title = name,
            number = number.toString(),
            numberSort = number.toFloat()
        )

    fun fileName(): String = FilenameUtils.getName(url.toString())

    fun fileExtension(): String = FilenameUtils.getExtension(url.toString())

    fun path(): Path = Paths.get(this.url.toURI())

    fun fileSizeHumanReadable(): String = BinaryByteUnit.format(fileSize)

    override fun toString(): String = "Book($id, ${url.toURI().path})"
}