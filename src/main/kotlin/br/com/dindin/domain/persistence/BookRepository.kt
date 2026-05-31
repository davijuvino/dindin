package br.com.dindin.domain.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import br.com.dindin.domain.model.Book
import br.com.dindin.domain.model.Library
import br.com.dindin.domain.model.Media
import org.springframework.stereotype.Repository
import java.net.URL


@Repository
interface BookRepository : JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
  //@QueryHints(QueryHint(name = CACHEABLE, value = "true"))
  override fun findAll(pageable: Pageable): Page<Book>

  //@QueryHints(QueryHint(name = CACHEABLE, value = "true"))
  fun findAllBySeriesId(seriesId: Long, pageable: Pageable): Page<Book>

  //@QueryHints(QueryHint(name = CACHEABLE, value = "true"))
  fun findAllByMediaStatusInAndSeriesId(status: Collection<Media.Status>, seriesId: Long, pageable: Pageable): Page<Book>

  //@QueryHints(QueryHint(name = CACHEABLE, value = "true"))
  fun findBySeriesLibraryIn(seriesLibrary: Collection<Library>, pageable: Pageable): Page<Book>

  fun findBySeriesLibraryIn(seriesLibrary: Collection<Library>): List<Book>
  fun findBySeriesLibrary(seriesLibrary: Library): List<Book>

  fun findByUrl(url: URL): Book?
  fun findAllByMediaStatusAndSeriesLibrary(status: Media.Status, library: Library): List<Book>
  fun findAllByMediaThumbnailIsNull(): List<Book>
}
