package com.forrestgump.ig.ui.screens.chat

import com.forrestgump.ig.data.models.Chat

data class UiState(
    val chats: List<Chat> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showNewMessageScreen: Boolean = false
)