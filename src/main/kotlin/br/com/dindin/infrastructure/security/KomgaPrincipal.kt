package br.com.dindin.infrastructure.security

import br.com.dindin.domain.model.ApiKey
import br.com.dindin.domain.model.KomgaUser
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.core.user.OAuth2User

class KomgaPrincipal(
    val user: KomgaUser,
    val oAuth2User: OAuth2User? = null,
    val oidcUser: OidcUser? = null,
    val apiKey: ApiKey? = null,
    private val name: String = user.email,
) : UserDetails {

  override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
    return user.roles.map { it.name }
      .toMutableSet()
      .apply { add("USER") }
      .map { SimpleGrantedAuthority("ROLE_$it") }
      .toMutableSet()
  }

  override fun isEnabled() = true

  override fun getUsername() = user.email

  override fun isCredentialsNonExpired() = true

  override fun getPassword() = user.password

  override fun isAccountNonExpired() = true

  override fun isAccountNonLocked() = true
}