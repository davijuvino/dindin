package br.com.dindin.domain.persistence

import br.com.dindin.domain.model.ApiKey
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ApiKeyRepository : CrudRepository<ApiKey, Long> {
    fun existsByCommentAndUserId_Id(comment: String, userId: Long): Boolean
    fun findByUserId_Id(userId: Long): Collection<ApiKey>
    fun existsByIdAndUserId_Id(id: Long, userId: Long): Boolean
    fun deleteByIdAndUserId_Id(id: Long, userId: Long)
    fun findByPkey(pkey: String): ApiKey?
}
