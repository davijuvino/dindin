package br.com.dindin.interfaces


import br.com.dindin.domain.model.ContentRestrictions
import br.com.dindin.domain.model.KomgaUser
import br.com.dindin.domain.model.UserEmailAlreadyExistsException
import br.com.dindin.domain.model.UserRoles
import br.com.dindin.domain.persistence.KomgaUserRepository
import br.com.dindin.domain.persistence.LibraryRepository
import br.com.dindin.domain.service.KomgaUserLifecycle
import br.com.dindin.infrastructure.security.KomgaPrincipal
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.core.env.Environment
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("api/v1/users", produces = [MediaType.APPLICATION_JSON_VALUE])
class UserController(
    private val userLifecycle: KomgaUserLifecycle,
    private val userRepository: KomgaUserRepository,
    private val libraryRepository: LibraryRepository,
    env: Environment
) {

    private val demo = env.activeProfiles.contains("demo")

    @GetMapping("me")
    fun getCurrentUser(
        @AuthenticationPrincipal principal: KomgaPrincipal,
        @RequestParam(name = "remember-me", required = false) rememberMe: Boolean?,
    ): UserDto = principal.toDto()

    @PatchMapping("me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updatePasswordForCurrentUser(
        @AuthenticationPrincipal principal: KomgaPrincipal,
        @Valid @RequestBody
        newPasswordDto: PasswordUpdateDto,
    ) {
        if (demo) throw ResponseStatusException(HttpStatus.FORBIDDEN)
        userRepository.findByEmailIgnoreCase(principal.username)?.let { user ->
            userLifecycle.updatePassword(user, newPasswordDto.password, false)
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @GetMapping
    fun getUsers(): List<UserDto> = userRepository.findAll().map { it.toDto() }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addUser(
        @Valid @RequestBody
        newUser: UserCreationDto,
    ): UserDto =
        try {
            userLifecycle
                .createUser(
                    with(newUser) {
                        KomgaUser(
                            email = email,
                            password = password,
                            roles = UserRoles.Companion.valuesOf(roles)
                        )
                    },
                ).toDto()
        } catch (_: UserEmailAlreadyExistsException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A user with this email already exists")
        }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateUserById(
        @PathVariable id: Long,
        @Valid @RequestBody
        patch: UserUpdateDto,
        @AuthenticationPrincipal principal: KomgaPrincipal,
    ) {
        userRepository.findByIdOrNull(id)?.let { existing ->
            val updatedUser =
                with(patch) {
                    // calcula novos valores de restrictions de forma segura
                    val newAgeRestriction = if (isSet("ageRestriction")) {
                        if (ageRestriction == null || ageRestriction?.restriction == AllowExcludeDto.NONE)
                            null
                        else
                        // espera-se que o DTO tenha um método toDomain() que retorne AgeRestriction
                            ageRestriction!!.toDomain()
                    } else {
                        existing.restrictions.ageRestriction
                    }

                    val newLabelsAllow = if (isSet("labelsAllow")) labelsAllow ?: emptySet() else existing.restrictions.labelsAllow
                    val newLabelsExclude = if (isSet("labelsExclude")) labelsExclude ?: emptySet() else existing.restrictions.labelsExclude

                    existing.copy(
                        roles = if (isSet("roles")) UserRoles.valuesOf(roles!!) else existing.roles,
                        sharedAllLibraries = if (isSet("sharedLibraries")) sharedLibraries!!.all else existing.sharedAllLibraries,
                        restrictions =
                            ContentRestrictions(
                                ageRestriction = newAgeRestriction,
                                paramLabelsAllow = newLabelsAllow,
                                paramLabelsExclude = newLabelsExclude,
                            ),
                    )
                }
            userLifecycle.updateUser(updatedUser)
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    data class UserWithSharedLibrariesDto(
        val id: Long,
        val email: String,
        val roles: List<String>,
        val sharedAllLibraries: Boolean,
        val sharedLibraries: List<SharedLibraryDto>
    )

    data class SharedLibraryDto(
        val id: Long,
        val name: String
    )

    data class PasswordUpdateDto(
        @get:NotBlank val password: String
    )

    data class SharedLibrariesUpdateDto(
        val all: Boolean,
        val libraryIds: Set<Long>
    )
}