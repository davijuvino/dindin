package br.com.dindin.domain.persistence

import br.com.dindin.domain.model.AuthenticationActivity
import br.com.dindin.domain.model.KomgaUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

interface AuthenticationActivityRepository : JpaRepository<AuthenticationActivity, Long> {
    override fun findAll(pageable: Pageable): Page<AuthenticationActivity>

    fun findAllByUserId(
        userId: KomgaUser,
        pageable: Pageable,
    ): Page<AuthenticationActivity>

    fun findMostRecentByUserId(
        userId: KomgaUser,
        apiKeyId: Long,
    ): AuthenticationActivity?

    fun deleteByUserId(userId: KomgaUser)

    @Modifying
    @Transactional
    @Query("DELETE FROM AuthenticationActivity a WHERE a.dateTime < :dateTime")
    fun deleteOlderThan(@Param("dateTime") dateTime: LocalDateTime)
}
