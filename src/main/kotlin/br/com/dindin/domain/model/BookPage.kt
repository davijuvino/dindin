package br.com.dindin.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class BookPage(
    @Column(name = "file_name", nullable = false)
    val fileName: String,

    @Column(name = "media_type", nullable = false)
    val mediaType: String
)