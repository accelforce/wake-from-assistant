package net.accelf.wakefromassistant.helpers

fun String.toLongMacAddress() =
    filterNot { it in "-:" }
        .takeIf { it.length == 12 }
        ?.toLong(16)

fun Long.toStringMacAddress() =
    toString(16).padStart(12, '0')
        .chunked(2).joinToString(":")
