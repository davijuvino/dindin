package br.com.dindin.infrastructure.security.apikey

import br.com.dindin.infrastructure.security.TokenEncoder
import jakarta.servlet.http.HttpServletRequest
import br.com.dindin.infrastructure.hash.Hasher
import org.springframework.security.authentication.AuthenticationDetailsSource
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationConverter

/**
 * A strategy that retrieves the API key from the [headerName],
 * and convert it to an [ApiKeyAuthenticationToken]
 *
 * @property headerName the header name from which to retrieve the API key
 * @property hasher the hasher to use to encode the API key as username in the [Authentication] object
 * @property tokenEncoder the encoder to use to encode the API key in the [Authentication] object
 * @property authenticationDetailsSource the [AuthenticationDetailsSource] to enrich the [Authentication] details
 */
class HeaderApiKeyAuthenticationConverter(
    private val headerName: String,
    private val hasher: Hasher,
    private val tokenEncoder: TokenEncoder,
    private val authenticationDetailsSource: AuthenticationDetailsSource<HttpServletRequest, *>,
) : AuthenticationConverter {
  override fun convert(request: HttpServletRequest): Authentication? =
    request
      .getHeader(headerName)
      ?.let {
        val maskedToken = hasher.computeHash(it)
        val hashedToken = tokenEncoder.encode(it)
        ApiKeyAuthenticationToken.Companion
          .unauthenticated(maskedToken, hashedToken)
          .apply { details = authenticationDetailsSource.buildDetails(request) }
      }
}
