package com.y9vad9.todolist.composeui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.compose.LocalKoinScope
import org.koin.core.parameter.ParametersDefinition

/**
 * Creates and returns an instance of the specified state machine using the provided factory.
 *
 * @param MVI The reified type of the MVI Component to be created.
 * @return The created instance of the state machine.
 */
@Composable
inline fun <reified MVI> koinMviComponent(
    noinline parameters: ParametersDefinition? = null,
): MVI {
    val koin = LocalKoinScope.current.getKoin()

    return remember(parameters) {
        koin.get(qualifier = null, parameters)
    }
}