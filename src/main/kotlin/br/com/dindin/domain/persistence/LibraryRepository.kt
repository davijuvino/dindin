package br.com.dindin.domain.persistence


import br.com.dindin.domain.model.Library
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.stereotype.Repository


@Repository
interface LibraryRepository : JpaRepository<Library, Long> {

}
