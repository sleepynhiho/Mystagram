package com.forrestgump.ig.utils.constants

fun Long.formatAsElapsedTime(): String {
    val elapsedTime = System.currentTimeMillis() - this
    return when {
        elapsedTime < 60_000 -> "${elapsedTime / 1_000}s"
        elapsedTime < 3_600_000 -> "${elapsedTime / 60_000}m"
        elapsedTime < 86_400_000 -> "${elapsedTime / 3_600_000}h"
        elapsedTime < 31_536_000_000 -> "${elapsedTime / 86_400_000}d"
        else -> "${elapsedTime /31_536_000_000}y"
    }
}
