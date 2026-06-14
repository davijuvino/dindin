package br.com.dindin.infrastructure.hash

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.io.InputStream
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.inputStream

private val logger = KotlinLogging.logger {}

private const val DEFAULT_BUFFER_SIZE = 8192

@Component
class Hasher {
    fun computeHash(path: Path): String {
        logger.debug { "Hashing: $path" }
        return computeHash(path.inputStream())
    }

    fun computeHash(string: String): String = computeHash(string.byteInputStream())

    fun computeHash(stream: InputStream): String {
        val digest = MessageDigest.getInstance("SHA-256")

        stream.use {
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var len = it.read(buffer)
            while (len > 0) {
                digest.update(buffer, 0, len)
                len = it.read(buffer)
            }
        }

        return digest.digest().toHexString()
    }

    private fun ByteArray.toHexString(): String =
        joinToString("") { "%02x".format(it.toInt() and 0xff) }
}
