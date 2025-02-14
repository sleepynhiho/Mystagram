package com.forrestgump.ig.ui.screens.messages

import com.forrestgump.ig.utils.models.Conversation

data class UiState(
    val conversations: List<Conversation> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showNewMessageScreen: Boolean = false
)