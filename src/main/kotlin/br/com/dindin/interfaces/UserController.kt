package br.com.dindin.interfaces


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
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
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
    //@Operation(summary = "Retrieve current user", tags = [TagNames.CURRENT_USER])
    fun getCurrentUser(
        @AuthenticationPrincipal principal: KomgaPrincipal,
        @RequestParam(name = "remember-me", required = false) rememberMe: Boolean?,
    ): UserDto = principal.toDto()

    @PatchMapping("me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //@Operation(summary = "Update current user's password", tags = [TagNames.CURRENT_USER])
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
    //@PreAuthorize("hasRole('ADMIN')")
    //@Operation(summary = "List users", tags = [TagNames.USERS])
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

    /*
    data class UserCreationDto(
        @get:Email val email: String,
        @get:NotBlank val password: String,
        val roles: List<String> = emptyList()
    ) {
        fun toUserDetails(): UserDetails =
            User.withUsername(email)
                .password(password)
                .roles(*roles.toTypedArray())
                .build()
    }*/

    data class PasswordUpdateDto(
        @get:NotBlank val password: String
    )

    data class SharedLibrariesUpdateDto(
        val all: Boolean,
        val libraryIds: Set<Long>
    )
}