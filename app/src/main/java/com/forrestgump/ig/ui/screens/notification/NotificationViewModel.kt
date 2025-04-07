package com.forrestgump.ig.ui.screens.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forrestgump.ig.data.models.Notification
import com.forrestgump.ig.data.repositories.NotificationRepository
import com.forrestgump.ig.ui.screens.profile.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
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


    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications


    fun observeNotifications(currentUserId: String) {
        viewModelScope.launch {
            notificationRepository.observeNotifications(currentUserId).collect { newNotifications ->
                _notifications.value = newNotifications
            }
        }
    }
}