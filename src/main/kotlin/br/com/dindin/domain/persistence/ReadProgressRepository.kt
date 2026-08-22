package br.com.dindin.domain.persistence

import br.com.dindin.domain.model.ReadProgress
import org.springframework.data.jpa.repository.JpaRepository

interface ReadProgressRepository: JpaRepository<ReadProgress, Long> {
    fun findByBookIdAndUserId(bookId: Long, userId: Long): ReadProgress?
    fun findAllByBookIdInAndUserId(bookIds: Collection<Long>, userId: Long): List<ReadProgress>
    fun deleteByUserId(userId: Long)
    fun deleteByBookId(bookId: Long)
}