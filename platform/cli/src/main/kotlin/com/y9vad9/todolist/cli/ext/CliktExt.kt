package com.y9vad9.todolist.cli.ext

import com.github.ajalt.clikt.core.Abort
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.options.NullableOption
import com.github.ajalt.clikt.parameters.options.OptionCallTransformContext
import com.github.ajalt.clikt.parameters.options.OptionWithValues
import com.github.ajalt.clikt.parameters.options.transformAll

fun <T : Any> NullableOption<T, T>.multilinePromptUntil(
    startMessage: String,
    endMarker: String = ":q",
    default: T,
): OptionWithValues<T, T, T> = transformAll { invocations ->
    val provided = invocations.lastOrNull()
    if (provided != null) return@transformAll provided
    if (context.errorEncountered) throw Abort()

    val terminal = context.terminal
    terminal.println(startMessage)

    val inputLines = buildList {
        while (true) {
            terminal.print("> ")
            val line = terminal.readLineOrNull(false) ?: break
            if (line.trim() == endMarker) break
            add(line)
        }
    }

    val fullInput = inputLines.joinToString("\n").ifBlank {
        return@transformAll default
    }

    val ctx = OptionCallTransformContext(fullInput, this, context)

    try {
        val transformed = transformEach(ctx, listOf(transformValue(ctx, fullInput)))
        val validator = (option as? OptionWithValues<T, T, T>)?.transformValidator
        validator?.invoke(this, transformed)
        transformed
    } catch (e: UsageError) {
        e.context = e.context ?: context
        throw e
    }
}