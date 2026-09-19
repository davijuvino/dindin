package br.com.dindin.domain.persistence

import br.com.dindin.domain.model.ReadProgress
import org.springframework.data.jpa.repository.JpaRepository

interface ReadProgressRepository : JpaRepository<ReadProgress, Long> {
    fun findByBookIdAndUser_Id(bookId: String, userId: Long): ReadProgress?
    fun findAllByBookIdInAndUser_Id(bookIds: Collection<String>, userId: Long): List<ReadProgress>
    fun deleteByUser_Id(userId: Long)
    fun deleteByBookId(bookId: String)
}
