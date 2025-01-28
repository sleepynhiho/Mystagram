package com.forrestgump.ig.ui.screens.notification

import androidx.lifecycle.ViewModel
import com.forrestgump.ig.ui.screens.profile.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
) : ViewModel() {

    var uiState = MutableStateFlow(UiState())
        private set

    private fun clearUiState() {
        uiState.update { UiState() }
    }

    override fun onCleared() {
        super.onCleared()
        clearUiState()
    }
}