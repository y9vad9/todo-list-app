package com.y9vad9.todolist.ios

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.y9vad9.todolist.common.TodoComposeApp
import org.koin.mp.KoinPlatform
import kotlin.experimental.ExperimentalObjCName

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalObjCName::class)
@ObjCName("MainViewController")
fun MainViewController() = ComposeUIViewController {
    val rootContext = remember { DefaultComponentContext(LifecycleRegistry()) }
    TodoComposeApp(
        koin = KoinPlatform.getKoin(),
        rootComponentContext = rootContext,
        windowSizeClass = calculateWindowSizeClass(),
    )
}