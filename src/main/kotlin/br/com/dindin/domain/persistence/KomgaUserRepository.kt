package br.com.dindin.domain.persistence

import br.com.dindin.domain.model.KomgaUser
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface KomgaUserRepository : CrudRepository<KomgaUser, Long> {
    fun existsByEmailIgnoreCase(email: String): Boolean
    fun findByEmailIgnoreCase(email: String): KomgaUser?
}
