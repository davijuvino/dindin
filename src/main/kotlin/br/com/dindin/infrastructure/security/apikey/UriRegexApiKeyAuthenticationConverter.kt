package br.com.dindin.infrastructure.security.apikey

import br.com.dindin.infrastructure.security.TokenEncoder
import jakarta.servlet.http.HttpServletRequest
import br.com.dindin.infrastructure.hash.Hasher
import org.springframework.security.authentication.AuthenticationDetailsSource
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationConverter

/**
 * A strategy that uses a regex to retrieve the API key from the
 * request URI, and convert it to an [ApiKeyAuthenticationToken]
 *
 * @property tokenRegex the regex used to extract the API key
 * @property hasher the hasher to use to encode the API key as username in the [Authentication] object
 * @property tokenEncoder the encoder to use to encode the API key as credentials in the [Authentication] object
 * @property authenticationDetailsSource the [AuthenticationDetailsSource] to enrich the [Authentication] details
 */
class UriRegexApiKeyAuthenticationConverter(
    private val tokenRegex: Regex,
    private val hasher: Hasher,
    private val tokenEncoder: TokenEncoder,
    private val authenticationDetailsSource: AuthenticationDetailsSource<HttpServletRequest, *>,
) : AuthenticationConverter {
  override fun convert(request: HttpServletRequest): Authentication? =
    request.requestURI
      ?.let {
        tokenRegex.find(it)?.groupValues?.lastOrNull()
      }?.let {
        val maskedToken = hasher.computeHash(it)
        val hashedToken = tokenEncoder.encode(it)
        ApiKeyAuthenticationToken.Companion
          .unauthenticated(maskedToken, hashedToken)
          .apply { details = authenticationDetailsSource.buildDetails(request) }
      }
}
