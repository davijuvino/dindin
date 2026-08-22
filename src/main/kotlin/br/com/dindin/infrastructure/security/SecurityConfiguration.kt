package br.com.dindin.infrastructure.security

import br.com.dindin.infrastructure.configuration.KomgaProperties
import br.com.dindin.infrastructure.configuration.KomgaSettingsProvider
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

private val logger = KotlinLogging.logger {}

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfiguration(
    private val komgaSettingsProvider: KomgaSettingsProvider,
    private val komgaProperties: KomgaProperties,
    private val komgaUserDetailsService: UserDetailsService,
    private val userAgentWebAuthenticationDetailsSource: WebAuthenticationDetailsSource,
    private val sessionRegistry: SessionRegistry,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { cors ->
                cors.configurationSource(corsConfigurationSource())
            }
            .csrf { csrf -> csrf.disable() }

            // Headers de segurança modernos
            .headers { headers ->
                headers
                    .frameOptions { frameOptions -> frameOptions.sameOrigin() }
                    .xssProtection { xss ->
                        xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                    }
                    .contentSecurityPolicy { csp ->
                        csp.policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'")
                    }
            }

            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ADMIN")
                    .requestMatchers("/h2-console/**").permitAll()
                    //.requestMatchers("/api/**", "/opds/**").hasRole("USER")
                    .requestMatchers("/api/v1/users").permitAll()
                    .anyRequest().permitAll()
            }

            .httpBasic { basic -> basic.realmName("Komga") }

            .logout { logout ->
                logout
                    .logoutUrl("/api/v1/users/logout")
                    .logoutSuccessUrl("/")
                    .deleteCookies("JSESSIONID", "SESSION")
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
            }

            .sessionManagement { session ->
                session
                    .maximumSessions(10)
                    .maxSessionsPreventsLogin(false)
                    .sessionRegistry(sessionRegistry)
            }

        // Configuração moderna de Remember-Me
        if (!komgaProperties.rememberMe.key.isNullOrBlank()) {
            logger.info { "RememberMe is active, validity: ${komgaProperties.rememberMe.validity}s" }

            http.rememberMe { remember ->
                remember
                    .key(komgaProperties.rememberMe.key)
                    .tokenValiditySeconds(komgaProperties.rememberMe.validity)
                    .alwaysRemember(true)
                    .userDetailsService(komgaUserDetailsService)
                    .rememberMeParameter("remember-me")
                    .useSecureCookie(true)

            }
        }

        http.rememberMe {
            it.rememberMeServices(
                TokenBasedRememberMeServices(komgaSettingsProvider.getRememberMeKey(), komgaUserDetailsService).apply {
                    setTokenValiditySeconds(komgaSettingsProvider.rememberMeDuration.inWholeSeconds.toInt())
                    setAuthenticationDetailsSource(userAgentWebAuthenticationDetailsSource)
                    setCookieName("komga-remember-me")
                }
            )
        }

        return http.build()
    }


    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration(
                "/**",
                CorsConfiguration().apply {
                    allowedOriginPatterns = listOf("http://localhost:8081", "http://localhost:*")
                    allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD")
                    allowedHeaders = listOf("*")
                    exposedHeaders = listOf("Authorization", "Content-Disposition")
                    allowCredentials = true
                    maxAge = 3600L
                }
            )
        }
    }

    @Bean
    @Profile("dev")
    fun devCorsConfigurationSource(): CorsConfigurationSource {
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration(
                "/**",
                CorsConfiguration().apply {
                    allowedOriginPatterns = listOf("*")
                    allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD")
                    allowedHeaders = listOf("*")
                    allowCredentials = true
                    maxAge = 3600L
                }
            )
        }
    }
}