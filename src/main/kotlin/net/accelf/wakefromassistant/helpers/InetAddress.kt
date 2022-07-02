package net.accelf.wakefromassistant.helpers

import java.net.InetAddress

fun InetAddress.isReachable(): Result<Boolean> =
    runCatching { isReachable(5000) }
