package br.com.dindin.interfaces


import br.com.dindin.domain.model.ApiKey
import br.com.dindin.domain.model.KomgaUser
import org.gotson.komga.language.toUTCZoned
import java.time.ZonedDateTime

data class ApiKeyDto(
  val id: Long,
  val userId: Long,
  val key: String,
  val comment: String,
  val createdDate: ZonedDateTime,
  val lastModifiedDate: ZonedDateTime,
)

fun ApiKey.toDto() =
  ApiKeyDto(
    id = id,
    userId = userId?.id ?: 0L,
    key = pkey,
    comment = comment,
    createdDate = createdDate!!.toUTCZoned(),
    lastModifiedDate = createdDate!!.toUTCZoned(),
  )

fun ApiKeyDto.redacted() = copy(key = "*".repeat(6))
