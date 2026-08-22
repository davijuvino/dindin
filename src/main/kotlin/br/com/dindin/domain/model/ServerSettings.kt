package br.com.dindin.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "server_settings")
data class ServerSettings (
    val pKey: String,
    val pValue: String
){
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    var id: Long = 0
}

