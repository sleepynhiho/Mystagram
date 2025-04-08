package com.forrestgump.ig.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeManager: ThemeManager
) : ViewModel() {
    val currentTheme: StateFlow<String> = themeManager.currentTheme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = "system"
    )

    fun saveThemePreference(theme: String) {
        themeManager.saveThemePreference(theme)
    }
}