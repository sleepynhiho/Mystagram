package com.forrestgump.ig.utils.constants

import androidx.compose.ui.geometry.Offset

/** Function to format TimeStamp for stories
 * @return Returns a string with the formatted timeStamp for uploaded stories i.e "1m, "1h, etc"
 * */
fun Long.formatStoryTimeElapsed(): String {
    val timeDiff = System.currentTimeMillis() - this
    return when {
        timeDiff <= 60_000 -> "${timeDiff / 10_000}s"
        timeDiff <= 3_600_000 -> "${timeDiff / 60_000}m"
        timeDiff <= 86_400_000 -> "${timeDiff / 3_600_000}h"
        else -> "${timeDiff / 86_400_000}d"
    }
}

/**
 * Function to get dynamic alpha on drag for the background.
 * @param offsetY Requires the [Offset.y] dragged.
 * @return Returns the alpha value as per the offsetY
 */
fun calculateBackgroundAlpha(offsetY: Float): Float {
    return when(offsetY) {
        in -5f..10f -> 1f
        in 11f..100f -> 0.8f
        in 101f..500f -> 0.5f
        else -> 0f
    }
}

