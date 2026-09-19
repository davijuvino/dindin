package br.com.dindin.interfaces

import br.com.dindin.domain.model.ApiKey
import java.time.ZoneOffset
import java.time.ZonedDateTime

data class ApiKeyDto(val id: Long, val userId: Long, val key: String, val comment: String, val createdDate: ZonedDateTime, val lastModifiedDate: ZonedDateTime)

fun ApiKey.toDto() = ApiKeyDto(id, user?.id ?: 0L, pkey, comment, createdDate!!.atZone(ZoneOffset.UTC), lastModifiedDate.atZone(ZoneOffset.UTC))
fun ApiKeyDto.redacted() = copy(key = "*".repeat(6))
