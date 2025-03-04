package com.forrestgump.ig.ui.screens.addPost

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(
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
