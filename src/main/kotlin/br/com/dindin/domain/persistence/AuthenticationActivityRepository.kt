package br.com.dindin.domain.persistence

import br.com.dindin.domain.model.AuthenticationActivity
import br.com.dindin.domain.model.KomgaUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface AuthenticationActivityRepository : JpaRepository<AuthenticationActivity, Long> {
    override fun findAll(pageable: Pageable): Page<AuthenticationActivity>

}