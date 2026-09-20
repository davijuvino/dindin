package br.com.dindin.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "server_settings")
open class ServerSettings(
    @Column(name = "p_key", nullable = false)
    open var pKey: String = "",

    @Column(name = "p_value", nullable = false)
    open var pValue: String = "",
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long = 0

    constructor() : this(pKey = "", pValue = "")
}
