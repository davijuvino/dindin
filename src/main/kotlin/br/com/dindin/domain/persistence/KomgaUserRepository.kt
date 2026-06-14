package br.com.dindin.domain.persistence


import br.com.dindin.domain.model.ApiKey
import br.com.dindin.domain.model.KomgaUser
import br.com.dindin.domain.model.Library
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface KomgaUserRepository : CrudRepository<KomgaUser, Long> {

    fun findByApiKey(apiKey: Any): Pair<KomgaUser, ApiKey>?
    fun save(komgaUser: KomgaUser): KomgaUser
    fun save(komgaUser: ApiKey): ApiKey
    fun existsByEmailIgnoreCase(email: String): Boolean
    fun findByEmailIgnoreCase(email: String): KomgaUser?
    fun existsApiKeyByCommentAndUserId(comment: String, id: Long): Boolean

}
