package com.y9vad9.todolist.cli.commands

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.parameters.options.versionOption

class MainCommand : SuspendingCliktCommand("todolist") {
    override val printHelpOnEmptyArgs: Boolean = true

    init {
        versionOption("1.0")
    }

    override suspend fun run() {
        // nothing here, just a stub, shouldn't be called
    }
}