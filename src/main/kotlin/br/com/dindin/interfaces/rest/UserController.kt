package br.com.dindin.interfaces.rest

import br.com.dindin.domain.model.AgeRestriction
import br.com.dindin.domain.model.ContentRestrictions
import br.com.dindin.domain.model.KomgaUser
import br.com.dindin.domain.model.UserEmailAlreadyExistsException
import br.com.dindin.domain.model.UserRoles
import br.com.dindin.domain.persistence.KomgaUserRepository
import br.com.dindin.domain.persistence.LibraryRepository
import br.com.dindin.domain.service.KomgaUserLifecycle
import br.com.dindin.interfaces.rest.dto.AllowExcludeDto
import br.com.dindin.interfaces.rest.dto.UserCreationDto
import jakarta.validation.Valid
import br.com.dindin.interfaces.rest.dto.UserDto
import br.com.dindin.interfaces.rest.dto.toDto

import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api/v1/users", produces = [MediaType.APPLICATION_JSON_VALUE])
class UserController(
    private val userLifecycle: KomgaUserLifecycle,
    private val userRepository: KomgaUserRepository,
    private val libraryRepository: LibraryRepository,
    //private val authenticationActivityRepository: AuthenticationActivityRepository,
    env: Environment,
) {
  private val demo = env.activeProfiles.contains("demo")


  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  //@PreAuthorize("hasRole('ADMIN')")
  //@Operation(summary = "Create user", tags = [TagNames.USERS])
  fun addUser(
      @Valid @RequestBody
    newUser: UserCreationDto,
  ): UserDto =
    try {
      userLifecycle
        .createUser(
          with(newUser) {
              KomgaUser(
                  email,
                  password,
                  roles = UserRoles.Companion.valuesOf(roles),
                  // keep existing behaviour before those properties were added, by default new user has access to all libraries
                  sharedAllLibraries = sharedLibraries == null || sharedLibraries.all,
                  sharedLibrariesIds =
                      if (sharedLibraries == null || sharedLibraries.all)
                          emptySet()
                      else
                          libraryRepository.findAllById(sharedLibraries.libraryIds).map { it.id }.toSet(),
                  // keep existing behaviour before those properties were added, by default no restrictions are applied
                  restrictions =
                      ContentRestrictions(
                          ageRestriction =
                              if (ageRestriction == null || ageRestriction.restriction == AllowExcludeDto.NONE)
                                  null
                              else
                                  AgeRestriction(ageRestriction.age, ageRestriction.restriction.toDomain()),
                          labelsAllow = labelsAllow ?: emptySet(),
                          labelsExclude = labelsExclude ?: emptySet(),
                      ),
              )
          },
        ).toDto()
    } catch (_: UserEmailAlreadyExistsException) {
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A user with this email already exists")
    }
}
