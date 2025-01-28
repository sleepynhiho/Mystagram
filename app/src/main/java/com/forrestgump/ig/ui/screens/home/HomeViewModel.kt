package com.forrestgump.ig.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import com.google.firebase.auth.FirebaseAuth
import com.forrestgump.ig.utils.models.Story
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
) : ViewModel() {

    var uiState = MutableStateFlow(UiState())
        private set


    fun updateStoryViews(story: Story) {

    }

    fun setShowStoryScreen(value: Boolean) {
        uiState.update { it.copy(showStoryScreen = value) }
    }

    private fun clearUiState() {
        uiState.update { UiState() }
    }

    override fun onCleared() {
        super.onCleared()
        clearUiState()
    }
}