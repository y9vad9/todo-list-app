package com.y9vad9.todolist.integration

import com.y9vad9.todolist.domain.repository.SettingsRepository
import com.y9vad9.todolist.domain.type.settings.AppLanguage
import com.y9vad9.todolist.presentation.mvi.settings.types.AppSettings
import com.y9vad9.todolist.presentation.mvi.settings.types.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path
import okio.SYSTEM

@OptIn(ExperimentalSerializationApi::class)
class FileSystemSettingsRepository(
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
    private val path: Path,
    private val json: Json = Json,
    private val defaultSettings: AppSettings = AppSettings(),
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
) : SettingsRepository {

    private val _settings = MutableStateFlow(defaultSettings)

    private val settingsInitialization: Deferred<Unit> = coroutineScope.async(Dispatchers.IO) {
        _settings.value = readSettings()
    }

    private suspend fun ensureSettingsInitialized() {
        settingsInitialization.await()
    }

    override suspend fun getSettings(): StateFlow<AppSettings> {
        ensureSettingsInitialized()
        return _settings
    }

    override suspend fun setSettings(settings: AppSettings) {
        fileSystem.createDirectories(path.parent ?: path)
        val raw = Settings(settings.language.name, settings.theme.name)
        val jsonString = json.encodeToString(Settings.serializer(), raw)
        fileSystem.write(path) {
            writeUtf8(jsonString)
        }
        _settings.value = settings
    }

    private suspend fun readSettings(): AppSettings = withContext(Dispatchers.IO) {
        if (!fileSystem.exists(path)) return@withContext defaultSettings
        return@withContext runCatching {
            fileSystem.read(path) {
                val content = readUtf8()
                json.decodeFromString(Settings.serializer(), content)
            }
        }.map {
            AppSettings(AppLanguage.valueOf(it.language), AppTheme.valueOf(it.theme))
        }.getOrDefault(defaultSettings)
    }

    @Serializable
    private data class Settings(
        val language: String,
        val theme: String,
    )
}
