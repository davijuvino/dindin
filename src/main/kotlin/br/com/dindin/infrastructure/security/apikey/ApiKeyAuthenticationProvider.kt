package br.com.dindin.infrastructure.security.apikey

import br.com.dindin.domain.persistence.ApiKeyRepository
import br.com.dindin.infrastructure.security.KomgaPrincipal
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class ApiKeyAuthenticationProvider(private val apiKeyRepository: ApiKeyRepository) : AbstractUserDetailsAuthenticationProvider() {
    override fun additionalAuthenticationChecks(userDetails: UserDetails, authentication: UsernamePasswordAuthenticationToken) = Unit
    override fun retrieveUser(username: String, authentication: UsernamePasswordAuthenticationToken): UserDetails =
        apiKeyRepository.findByPkey(authentication.credentials.toString())?.let { key ->
            key.user?.let { KomgaPrincipal(it, apiKey = key, name = authentication.name) }
        } ?: throw BadCredentialsException("Bad credentials")
    override fun createSuccessAuthentication(principal: Any, authentication: Authentication, user: UserDetails): Authentication =
        ApiKeyAuthenticationToken.authenticated(principal, authentication.credentials, user.authorities).apply { details = authentication.details }
    override fun supports(authentication: Class<*>): Boolean = ApiKeyAuthenticationToken::class.java.isAssignableFrom(authentication)
}
