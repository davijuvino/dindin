package br.com.dindin.domain

import java.time.LocalDateTime

interface Auditable {
  val createdDate: LocalDateTime
  val lastModifiedDate: LocalDateTime
}
