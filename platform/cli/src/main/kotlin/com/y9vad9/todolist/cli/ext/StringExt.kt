package com.y9vad9.todolist.cli.ext

internal fun String.ellipsize(maxLength: Int): String {
    require(maxLength >= 0) { "maxLength must be non-negative" }

    if (this.length <= maxLength) return this
    if (maxLength < 1) return ""

    val ellipsis = "…"
    val trimLength = maxLength - ellipsis.length
    return if (trimLength > 0) this.take(trimLength) + ellipsis else ellipsis
}
