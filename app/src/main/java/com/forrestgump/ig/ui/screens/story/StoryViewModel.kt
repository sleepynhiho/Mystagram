package com.forrestgump.ig.ui.screens.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forrestgump.ig.data.models.UserStory
import com.forrestgump.ig.data.repositories.StoryRepository
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _userStories = MutableLiveData<List<UserStory>>()
    val userStories: LiveData<List<UserStory>> = _userStories

    private val _deleteStoryResult = MutableLiveData<Result<Unit>>()
    val deleteStoryResult: LiveData<Result<Unit>> = _deleteStoryResult

    private var storyListener: ListenerRegistration? = null

    init {
        startObservingStories()
    }

    private fun startObservingStories() {
        // Remove any existing listener before creating a new one
        storyListener?.remove()
        
        storyListener = storyRepository.observeUserStories { stories ->
            _userStories.postValue(stories)
        }
    }

    // Keep this method for backward compatibility
    fun fetchUserStories() {
        // Instead of manual fetch, restart the listener to ensure we have the latest data
        startObservingStories()
    }

    fun deleteStory(userId: String, storyId: String) {
        viewModelScope.launch {
            _deleteStoryResult.value = storyRepository.deleteStory(userId, storyId)
            // Fetch stories again after successful deletion
            fetchUserStories()
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up the listener when ViewModel is cleared
        storyListener?.remove()
        storyListener = null
    }
}
