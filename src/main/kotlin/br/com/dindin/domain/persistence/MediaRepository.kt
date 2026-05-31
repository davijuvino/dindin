package br.com.dindin.domain.persistence


import br.com.dindin.domain.model.Media
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MediaRepository : JpaRepository<Media, Long>
