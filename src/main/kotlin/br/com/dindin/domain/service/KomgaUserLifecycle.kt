package br.com.dindin.domain.service

import br.com.dindin.domain.model.KomgaUser
import br.com.dindin.domain.model.UserEmailAlreadyExistsException
import br.com.dindin.domain.persistence.KomgaUserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class KomgaUserLifecycle(
    private val userRepository: KomgaUserRepository,

    private val passwordEncoder: PasswordEncoder,

    ) {


  @Throws(UserEmailAlreadyExistsException::class)
  fun createUser(komgaUser: KomgaUser): KomgaUser {
    if (userRepository.existsByEmailIgnoreCase(komgaUser.email)) throw UserEmailAlreadyExistsException("A user with the same email already exists: ${komgaUser.email}")
        komgaUser.password = passwordEncoder.encode(komgaUser.password)
        userRepository.save(komgaUser)

    val createdUser = userRepository.findByIdOrNull(komgaUser.id)!!
    logger.info { "User created: $createdUser" }
    return createdUser
  }



}
