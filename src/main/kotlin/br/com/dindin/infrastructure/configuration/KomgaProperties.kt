package br.com.dindin.infrastructure.configuration

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated


@Component
@ConfigurationProperties(prefix = "komga")
@Validated
class KomgaProperties {
  var librariesScanCron: String = ""

  var librariesScanStartup: Boolean = false

  var librariesScanDirectoryExclusions: List<String> = emptyList()

  var filesystemScannerForceDirectoryModifiedTime: Boolean = false

  var threads = Threads()

  class Threads {
    @Min(1)
    @Deprecated("Deprecated since 0.28")
    var analyzer: Int = 2
  }

  var rememberMe = RememberMe()

  class RememberMe {
    @NotBlank
    var key: String? = null

    @Positive
    var validity: Int = 1209600 // 2 weeks
  }
}
