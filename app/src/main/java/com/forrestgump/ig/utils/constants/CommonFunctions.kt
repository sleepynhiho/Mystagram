package com.forrestgump.ig.utils.constants

import java.util.Date

fun Date.formatAsElapsedTime(): String {
    val elapsedTime = System.currentTimeMillis() - this.time
    if (elapsedTime < 0) return "0s"

    val secondMillis = 1_000L
    val minuteMillis = 60 * secondMillis
    val hourMillis = 60 * minuteMillis
    val dayMillis = 24 * hourMillis
    val yearMillis = (365.25 * dayMillis).toLong()

    return when {
        elapsedTime < minuteMillis -> "${elapsedTime / secondMillis}s"
        elapsedTime < hourMillis -> "${elapsedTime / minuteMillis}m"
        elapsedTime < dayMillis -> "${elapsedTime / hourMillis}h"
        elapsedTime < yearMillis -> "${elapsedTime / dayMillis}d"
        else -> "${elapsedTime / yearMillis}y"
    }
}
