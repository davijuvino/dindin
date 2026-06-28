package br.com.dindin.domain.persistence

import br.com.dindin.domain.model.ServerSettings
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface ServerSettingsRepository : JpaRepository<ServerSettings, Long>{
    fun findByPKey(pKey: String): ServerSettings?
}