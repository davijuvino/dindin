package br.com.dindin.domain.service

import br.com.dindin.domain.model.KomgaUser
import br.com.dindin.domain.persistence.KomgaUserRepository
import io.github.oshai.kotlinlogging.KotlinLogging

import br.com.dindin.domain.model.UserEmailAlreadyExistsException

import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate

private val logger = KotlinLogging.logger {}

@Service
class KomgaUserLifecycle(
    private val userRepository: KomgaUserRepository,
    private val passwordEncoder: PasswordEncoder,

) {

  @Throws(UserEmailAlreadyExistsException::class)
  fun createUser(komgaUser: KomgaUser): KomgaUser {
    if (userRepository.existsByEmailIgnoreCase(komgaUser.email)) throw UserEmailAlreadyExistsException("A user with the same email already exists: ${komgaUser.email}")

    userRepository.save(komgaUser.copy(password = passwordEncoder.encode(komgaUser.password)))

    val createdUser = userRepository.findByIdOrNull(komgaUser.id)
    logger.info { "User created: $createdUser" }
    return createdUser
  }


}
