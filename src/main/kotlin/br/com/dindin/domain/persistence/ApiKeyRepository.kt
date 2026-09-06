package br.com.dindin.domain.persistence

import br.com.dindin.domain.model.ApiKey
import org.springframework.data.jpa.repository.JpaRepository

interface ApiKeyRepository : JpaRepository<ApiKey, Long>  {
}