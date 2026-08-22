package br.com.dindin.interfaces

import br.com.dindin.domain.model.ApiKey
import br.com.dindin.domain.model.AuthenticationActivity
import br.com.dindin.domain.model.KomgaUser
import com.fasterxml.jackson.annotation.JsonFormat
import org.gotson.komga.language.toUTC
import java.time.LocalDateTime

data class AuthenticationActivityDto(
  val userId: KomgaUser,
  val email: String?,
  val apiKeyId: ApiKey,
  val apiKeyComment: String? = null,
  val ip: String?,
  val userAgent: String?,
  val success: Boolean,
  val error: String?,
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  val dateTime: LocalDateTime,
  val source: String?,
)

fun AuthenticationActivity.toDto() =
  AuthenticationActivityDto(
    userId = userId,
    email = email,
    apiKeyId = apiKeyId,
    apiKeyComment = apiKeyComment,
    ip = ip,
    userAgent = userAgent,
    success = success,
    error = error,
    dateTime = dateTime.toUTC(),
    source = source,
  )
