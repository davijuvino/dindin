package br.com.dindin.domain.persistence

import br.com.dindin.domain.model.SyncPoint
import org.springframework.data.jpa.repository.JpaRepository

interface SyncPointRepository : JpaRepository<SyncPoint, Long> {

}