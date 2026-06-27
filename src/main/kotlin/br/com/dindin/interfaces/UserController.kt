package br.com.dindin.interfaces


import br.com.dindin.domain.model.AgeRestriction
import br.com.dindin.domain.model.ContentRestrictions
import br.com.dindin.domain.model.DuplicateNameException
import br.com.dindin.domain.model.KomgaUser
import br.com.dindin.domain.model.UserEmailAlreadyExistsException
import br.com.dindin.domain.model.UserRoles
import br.com.dindin.domain.persistence.AuthenticationActivityRepository
import br.com.dindin.domain.persistence.KomgaUserRepository
import br.com.dindin.domain.persistence.LibraryRepository
import br.com.dindin.domain.service.KomgaUserLifecycle
import br.com.dindin.infrastructure.security.KomgaPrincipal
import br.com.dindin.infrastructure.util.UnpagedSorted
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springdoc.core.converters.models.PageableAsQueryParam
import org.springframework.core.env.Environment
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.DeleteMapping
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
    private val authenticationActivityRepository: AuthenticationActivityRepository,
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

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //@PreAuthorize("hasRole('ADMIN') and #principal.user.id != #id")
    //@Operation(summary = "Delete user", tags = [TagNames.USERS])
    fun deleteUserById(
        @PathVariable id: Long,
        @AuthenticationPrincipal principal: KomgaPrincipal,
    ) {
        userRepository.findByIdOrNull(id)?.let {
            userLifecycle.deleteUser(it)
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
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
                            AgeRestriction.fromMinAge(ageRestriction?.age)
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

    @PatchMapping("{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //@PreAuthorize("hasRole('ADMIN') or #principal.user.id == #id")
    //@Operation(summary = "Update user's password", tags = [TagNames.USERS])
    fun updatePasswordByUserId(
        @PathVariable id: Long,
        @AuthenticationPrincipal principal: KomgaPrincipal,
        @Valid @RequestBody
        newPasswordDto: PasswordUpdateDto,
    ) {
        if (demo) throw ResponseStatusException(HttpStatus.FORBIDDEN)
        userRepository.findByIdOrNull(id)?.let { user ->
            userLifecycle.updatePassword(user, newPasswordDto.password, user.id != principal.user.id)
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @GetMapping("me/authentication-activity")
    @PageableAsQueryParam
    //@Operation(summary = "Retrieve authentication activity for the current user", tags = [TagNames.CURRENT_USER])
    fun getAuthenticationActivityForCurrentUser(
        @AuthenticationPrincipal principal: KomgaPrincipal,
        @RequestParam(name = "unpaged", required = false) unpaged: Boolean = false,
        @Parameter(hidden = true) page: Pageable,
    ): Page<AuthenticationActivityDto> {
        if (demo && !principal.user.isAdmin()) throw ResponseStatusException(HttpStatus.FORBIDDEN)
        val sort =
            if (page.sort.isSorted)
                page.sort
            else
                Sort.by(Sort.Order.desc("dateTime"))

        val pageRequest =
            if (unpaged)
                UnpagedSorted(sort)
            else
                PageRequest.of(
                    page.pageNumber,
                    page.pageSize,
                    sort,
                )

        return authenticationActivityRepository.findAllByUserId(principal.user, pageRequest).map { it.toDto() }
    }

    @GetMapping("{id}/authentication-activity/latest")
    //@PreAuthorize("hasRole('ADMIN') or #principal.user.id == #id")
    //@Operation(summary = "Retrieve latest authentication activity for a user", tags = [TagNames.USERS])
    fun getLatestAuthenticationActivityByUserId(
        @PathVariable id: Long,
        @AuthenticationPrincipal principal: KomgaPrincipal,
        @RequestParam(required = false, name = "apikey_id") apiKeyId: Long,
    ): AuthenticationActivityDto =
        userRepository.findByIdOrNull(id)?.let { user ->
            authenticationActivityRepository.findMostRecentByUserId(user, apiKeyId)?.toDto()
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    @GetMapping("me/api-keys")
    //@Operation(summary = "Retrieve API keys", tags = [TagNames.API_KEYS])
    fun getApiKeysForCurrentUser(
        @AuthenticationPrincipal principal: KomgaPrincipal,
    ): Collection<ApiKeyDto> {
        if (demo && !principal.user.isAdmin()) throw ResponseStatusException(HttpStatus.FORBIDDEN)
        return userRepository.findApiKeyByUserId(principal.user.id).map { it.toDto().redacted() }
    }

    @PostMapping("me/api-keys")
    //@Operation(summary = "Create API key", tags = [TagNames.API_KEYS])
    fun createApiKeyForCurrentUser(
        @AuthenticationPrincipal principal: KomgaPrincipal,
        @Valid @RequestBody apiKeyRequest: ApiKeyRequestDto,
    ): ApiKeyDto {
        if (demo && !principal.user.isAdmin()) throw ResponseStatusException(HttpStatus.FORBIDDEN)
        return try {
            userLifecycle.createApiKey(principal.user, apiKeyRequest.comment)?.toDto()
        } catch (e: DuplicateNameException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.code)
        }
            ?: throw ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to generate API key")
    }

    @DeleteMapping("me/api-keys/{keyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //@Operation(summary = "Delete API key", tags = [TagNames.API_KEYS])
    fun deleteApiKeyByKeyId(
        @AuthenticationPrincipal principal: KomgaPrincipal,
        @PathVariable keyId: Long,
    ) {
        if (!userRepository.existsApiKeyByIdAndUserId(keyId, principal.user.id))
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        userRepository.deleteApiKeyByIdAndUserId(keyId, principal.user.id)
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

    data class SharedLibrariesUpdateDto(
        val all: Boolean,
        val libraryIds: Set<Long>
    )
}