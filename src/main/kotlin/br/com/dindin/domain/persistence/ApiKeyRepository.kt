package br.com.dindin.domain.persistence

import br.com.dindin.domain.model.ApiKey
import br.com.dindin.domain.model.KomgaUser
import org.springframework.data.jpa.repository.JpaRepository

interface ApiKeyRepository : JpaRepository<ApiKey, Long> {
    fun findByPkey(pkey: String): ApiKey?
    fun findAllByUser_Id(userId: Long): Collection<ApiKey>
    fun existsByCommentAndUser_Id(comment: String, userId: Long): Boolean
    fun existsByIdAndUser_Id(id: Long, userId: Long): Boolean
    fun deleteByIdAndUser_Id(id: Long, userId: Long)
    fun deleteByUser_Id(userId: Long)
    fun existsByCommentAndUserId(string: kotlin.String, lng: kotlin.Long): kotlin.Boolean
}
