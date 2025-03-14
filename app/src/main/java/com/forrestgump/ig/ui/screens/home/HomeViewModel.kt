package com.forrestgump.ig.ui.screens.home

import androidx.lifecycle.ViewModel
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.data.models.UserStory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
) : ViewModel() {

    var uiState = MutableStateFlow(UiState())
        private set


    fun onStoryScreenClicked(value: Boolean, userStoryIndex: Int) {
        uiState.update { it.copy(showStoryScreen = value, userStoryIndex = userStoryIndex) }
    }

    private fun clearUiState() {
        uiState.update { UiState() }
    }

    override fun onCleared() {
        super.onCleared()
        clearUiState()
    }

    fun updateUserStories(newUserStories: List<UserStory>, currentUser: User) {
        val myStories = newUserStories.filter { it.userId == currentUser.userId }
        val otherUserStories = newUserStories.filter { it.userId != currentUser.userId }

        uiState.update {
            it.copy(
                myStories = myStories,
                userStories = otherUserStories
            )
        }
    }


}