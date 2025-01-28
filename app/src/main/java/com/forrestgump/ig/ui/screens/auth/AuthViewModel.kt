package com.forrestgump.ig.ui.screens.auth

import androidx.lifecycle.ViewModel
import com.forrestgump.ig.remote.models.AppUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
) : ViewModel() {

    var uiState = MutableStateFlow(UiState())
        private set

}