package com.forrestgump.ig.ui.screens.search

import androidx.lifecycle.ViewModel
import com.forrestgump.ig.ui.screens.profile.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
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