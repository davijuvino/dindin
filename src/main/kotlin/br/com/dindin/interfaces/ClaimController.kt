package br.com.dindin.interfaces

import br.com.dindin.domain.model.ContentRestrictions
import br.com.dindin.domain.model.KomgaUser
import br.com.dindin.domain.model.UserRoles
import br.com.dindin.domain.service.KomgaUserLifecycle
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

//import org.gotson.komga.infrastructure.openapi.OpenApiConfiguration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api/v1/claim", produces = [MediaType.APPLICATION_JSON_VALUE])
//@Tag(name = OpenApiConfiguration.TagNames.CLAIM)
@Validated
@SecurityRequirements
class ClaimController(
    private val userDetailsLifecycle: KomgaUserLifecycle,
) {
  @GetMapping
  @Operation(summary = "Retrieve claim status", description = "Check whether this server has already been claimed.")
  fun getClaimStatus() = ClaimStatus(userDetailsLifecycle.countUsers() > 0)

  @PostMapping
  @Operation(summary = "Claim server", description = "Creates an admin user with the provided credentials.")
  fun claimServer(
    @Email(regexp = ".+@.+\\..+")
    @RequestHeader("X-Komga-Email")
    email: String,
    @NotBlank
    @RequestHeader("X-Komga-Password")
    password: String,
  ): UserDto {
    if (userDetailsLifecycle.countUsers() > 0)
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, "This server has already been claimed")

      return userDetailsLifecycle
          .createUser(
              KomgaUser(
                  email = email,
                  password = password,
                  roles = UserRoles.entries.toSet(),
                  sharedLibrariesIds = emptySet(),
                  sharedAllLibraries = false,
                  restrictions = ContentRestrictions(),
                  userId = 0L,
                  comment = null
              ),
          ).toDto()
  }

  data class ClaimStatus(
    val isClaimed: Boolean,
  )
}
