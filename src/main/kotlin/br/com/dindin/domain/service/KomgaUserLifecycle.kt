package br.com.dindin.domain.service

import br.com.dindin.domain.model.ApiKey
import br.com.dindin.domain.model.DuplicateNameException
import br.com.dindin.domain.model.KomgaUser
import br.com.dindin.domain.model.UserEmailAlreadyExistsException
import br.com.dindin.domain.persistence.AuthenticationActivityRepository
import br.com.dindin.domain.persistence.KomgaUserRepository
import br.com.dindin.domain.persistence.ReadProgressRepository
import br.com.dindin.domain.persistence.SyncPointRepository
import br.com.dindin.domain.persistence.ApiKeyRepository
import br.com.dindin.infrastructure.security.KomgaPrincipal
import br.com.dindin.infrastructure.security.TokenEncoder
import br.com.dindin.infrastructure.security.apikey.ApiKeyGenerator
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate

private val logger = KotlinLogging.logger {}

@Service
class KomgaUserLifecycle(
    private val userRepository: KomgaUserRepository,
    private val apiKeyRepository: ApiKeyRepository,
    private val readProgressRepository: ReadProgressRepository,
    private val authenticationActivityRepository: AuthenticationActivityRepository,
    private val sessionRegistry: SessionRegistry,
    private val apiKeyGenerator: ApiKeyGenerator,
    private val syncPointRepository: SyncPointRepository,
    private val transactionTemplate: TransactionTemplate,
    private val passwordEncoder: PasswordEncoder,
    private val tokenEncoder: TokenEncoder,

    ) {


  @Throws(UserEmailAlreadyExistsException::class)
  fun createUser(komgaUser: KomgaUser): KomgaUser {
    if (userRepository.existsByEmailIgnoreCase(komgaUser.email)) throw UserEmailAlreadyExistsException("A user with the same email already exists: ${komgaUser.email}")

    val saved = userRepository.save(komgaUser.copy(password = passwordEncoder.encode(komgaUser.password)))
    logger.info { "User created: $saved" }
    return saved
  }


  fun countUsers() = userRepository.count()

  fun updateUser(user: KomgaUser) {
        val existing = userRepository.findByIdOrNull(user.id)
        requireNotNull(existing) { "User doesn't exist, cannot update: $user" }

      val toUpdate = user.copy(password = existing.password)
      logger.info { "Update user: $toUpdate" }
      userRepository.save(toUpdate)
  }

  fun updatePassword(
        user: KomgaUser,
        newPassword: String,
        expireSessions: Boolean,
  ) {
      logger.info { "Changing password for user ${user.email}" }
      val updatedUser = user.copy(password = passwordEncoder.encode(newPassword))
      userRepository.save(updatedUser)
  }

    fun expireSessions(user: KomgaUser) {
        logger.info { "Expiring all sessions for user: ${user.email}" }
        sessionRegistry
            .getAllSessions(KomgaPrincipal(user), false)
            .forEach {
                logger.info { "Expiring session: ${it.sessionId}" }
                it.expireNow()
            }
    }


  fun deleteUser(user: KomgaUser) {
        logger.info { "Deleting user: $user" }

        transactionTemplate.executeWithoutResult {
            readProgressRepository.deleteByUserId(user.id)
            authenticationActivityRepository.deleteById(user.id)
            syncPointRepository.deleteById(user.id)
            userRepository.deleteById(user.id)
        }

      expireSessions(user)
  }

    /**
     * Create and persist an API key for the user.
     * @return the ApiKey, or null if a unique API key could not be generated
     */
    fun createApiKey(
        user: KomgaUser,
        comment: String,
    ): ApiKey? {
        val commentTrimmed = comment.trim()
        if (userRepository.existsApiKeyByCommentAndUserId(commentTrimmed, user.id))
            throw DuplicateNameException("api key comment already exists for this user", "ERR_1034")
        for (attempt in 1..10) {
            try {
                val plainTextKey =
                    ApiKey(
                        userId = user,
                        pkey = apiKeyGenerator.generate(),
                        comment = commentTrimmed,
                    )
                apiKeyRepository.save(plainTextKey.copy(pkey = tokenEncoder.encode(plainTextKey.pkey)))
                return plainTextKey
            } catch (e: Exception) {
                logger.debug { "Failed to generate unique api key, attempt #$attempt" }
            }
        }
        return null
    }




}
