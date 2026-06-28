package br.com.dindin.infrastructure.configuration

import br.com.dindin.domain.model.ServerSettings
import br.com.dindin.domain.model.ThumbnailSize
import br.com.dindin.domain.persistence.ServerSettingsRepository
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

@Service
class KomgaSettingsProvider(
    private val serverSettingsRepository: ServerSettingsRepository,
    private val eventPublisher: ApplicationEventPublisher
) {

    fun getRememberMeKey(): String {
        return serverSettingsRepository.findByPKey(Settings.REMEMBER_ME_KEY.name)?.pValue
            ?: generateAndSaveRememberMeKey()
    }

    fun renewRememberMeKey(): String {
        return generateAndSaveRememberMeKey()
    }

    private fun generateAndSaveRememberMeKey(): String {
        val newKey = RandomStringUtils.secure().nextAlphanumeric(32)
        serverSettingsRepository.save(ServerSettings(Settings.REMEMBER_ME_KEY.name, newKey))
        return newKey
    }

    fun getThumbnailSize(): ThumbnailSize {
        return serverSettingsRepository.findByPKey(Settings.THUMBNAIL_SIZE.name)?.pValue?.let {
            ThumbnailSize.valueOf(it)
        } ?: ThumbnailSize.DEFAULT
    }

    fun setThumbnailSize(value: ThumbnailSize) {
        serverSettingsRepository.save(ServerSettings(Settings.THUMBNAIL_SIZE.name, value.name))
    }

    fun getTaskPoolSize(): Int {
        return serverSettingsRepository.findByPKey(Settings.TASK_POOL_SIZE.name)?.pValue?.toIntOrNull() ?: 1
    }

    fun setTaskPoolSize(value: Int) {
        serverSettingsRepository.save(ServerSettings(Settings.TASK_POOL_SIZE.name, value.toString()))
        eventPublisher.publishEvent("TaskPoolSizeChanged")
    }

    fun getServerPort(): Int? {
        return serverSettingsRepository.findByPKey(Settings.SERVER_PORT.name)?.pValue?.toIntOrNull()
    }

    fun setServerPort(value: Int?) {
        if (value != null) {
            serverSettingsRepository.save(ServerSettings(Settings.SERVER_PORT.name, value.toString()))
        } else {
            serverSettingsRepository.delete(ServerSettings(Settings.SERVER_PORT.name, value.toString()))
        }
    }

    fun getServerContextPath(): String? {
        return serverSettingsRepository.findByPKey(Settings.SERVER_CONTEXT_PATH.name)?.pValue
    }

    fun setServerContextPath(value: String?) {
        if (value != null) {
            serverSettingsRepository.save(ServerSettings(Settings.SERVER_CONTEXT_PATH.name, value))
        } else {
            serverSettingsRepository.delete(ServerSettings(Settings.SERVER_CONTEXT_PATH.name, value.toString()))
        }
    }

    fun isKoboProxyEnabled(): Boolean {
        return serverSettingsRepository.findByPKey(Settings.KOBO_PROXY.name)?.pValue?.toBoolean() == true
    }

    fun setKoboProxy(value: Boolean) {
        serverSettingsRepository.save(ServerSettings(Settings.KOBO_PROXY.name, value.toString()))
    }

    fun getKoboPort(): Int? {
        return serverSettingsRepository.findByPKey(Settings.KOBO_PORT.name)?.pValue?.toIntOrNull()
    }

    fun setKoboPort(value: Int?) {
        if (value != null) {
            serverSettingsRepository.save(ServerSettings(Settings.KOBO_PORT.name, value.toString()))
        } else {
            serverSettingsRepository.delete(ServerSettings(Settings.KOBO_PORT.name, value.toString()))
        }
    }

    var rememberMeDuration: Duration =
        serverSettingsRepository.findByPKey(Settings.REMEMBER_ME_DURATION.name)?.pValue?.toIntOrNull()?.days
            ?: 365.days
        set(value) {
            serverSettingsRepository.save(ServerSettings(Settings.REMEMBER_ME_DURATION.name, value.inWholeDays.toString()))
            field = value
        }

    fun getKepubifyPath(): String? {
        return serverSettingsRepository.findByPKey(Settings.KEPUBIFY_PATH.name)?.pValue?.ifBlank { null }
    }

    fun setKepubifyPath(value: String?) {
        if (value != null) {
            serverSettingsRepository.save(ServerSettings(Settings.KEPUBIFY_PATH.name, value))
        } else {
            serverSettingsRepository.delete(ServerSettings(Settings.KEPUBIFY_PATH.name, value.toString()))
        }
        eventPublisher.publishEvent("KepubifyPathChanged")
    }
}

private enum class Settings {
    REMEMBER_ME_KEY,
    REMEMBER_ME_DURATION,
    THUMBNAIL_SIZE,
    TASK_POOL_SIZE,
    SERVER_PORT,
    SERVER_CONTEXT_PATH,
    KOBO_PROXY,
    KOBO_PORT,
    KEPUBIFY_PATH,
}
