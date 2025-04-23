package com.y9vad9.todolist.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.defaultComponentContext
import com.y9vad9.todolist.common.TodoComposeApp
import com.y9vad9.todolist.domain.repository.SettingsRepository
import com.y9vad9.todolist.domain.repository.TimeZoneRepository
import org.koin.android.ext.android.inject
import org.koin.mp.KoinPlatform

class AppActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val componentContext = defaultComponentContext()

        setContent {
            TodoComposeApp(
                koin = KoinPlatform.getKoin(),
                rootComponentContext = componentContext,
                windowSizeClass = calculateWindowSizeClass(this)
            )
        }
    }
}