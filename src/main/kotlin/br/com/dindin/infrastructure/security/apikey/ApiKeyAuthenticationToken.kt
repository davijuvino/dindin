package br.com.dindin.infrastructure.security.apikey

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority

/**
 * Especialização de [UsernamePasswordAuthenticationToken] para armazenar chaves de API.
 */
class ApiKeyAuthenticationToken private constructor(
    principal: Any,
    credentials: Any?,
    authorities: Collection<GrantedAuthority>?,
) : UsernamePasswordAuthenticationToken(principal, credentials, authorities!!) {
    private constructor(principal: Any, credentials: Any) : this(principal, credentials, null) {
        isAuthenticated = false
    }

    companion object {
        fun authenticated(
            principal: Any,
            credentials: Any?,
            authorities: Collection<GrantedAuthority>?,
        ) = ApiKeyAuthenticationToken(principal, credentials, authorities)

        fun unauthenticated(
            principal: Any,
            credentials: Any,
        ) = ApiKeyAuthenticationToken(principal, credentials)
    }
}