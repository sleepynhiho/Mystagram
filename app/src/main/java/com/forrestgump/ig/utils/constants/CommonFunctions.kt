package com.forrestgump.ig.utils.constants

import android.app.Activity
import android.content.Context
import java.util.Date
import android.content.res.Configuration
import java.util.Locale

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

fun changeAppLanguage(context: Context, languageCode: String, shouldRecreate: Boolean = true) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val currentLang = prefs.getString("language_code", "en") ?: "en"

    // Nếu ngôn ngữ không thay đổi, không làm gì cả
    if (currentLang == languageCode) return

    // Lưu ngôn ngữ mới
    prefs.edit().putString("language_code", languageCode).apply()

    // Cập nhật locale
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val resources = context.resources
    val config = Configuration(resources.configuration)
    config.setLocale(locale)

    context.resources.updateConfiguration(config, resources.displayMetrics)

    // Chỉ gọi recreate nếu cần (tránh vòng lặp khi khởi động app)
    if (context is Activity && shouldRecreate) {
        context.recreate()
    }
}




fun saveLanguagePreference(context: Context, languageCode: String) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("language_code", languageCode).apply()
}
