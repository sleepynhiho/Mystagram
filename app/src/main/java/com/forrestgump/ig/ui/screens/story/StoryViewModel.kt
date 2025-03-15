package com.forrestgump.ig.ui.screens.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.forrestgump.ig.data.models.UserStory
import com.forrestgump.ig.data.repositories.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _userStories = MutableLiveData<List<UserStory>>()
    val userStories: LiveData<List<UserStory>> = _userStories

    fun fetchUserStories() {
        storyRepository.getUserStories { stories ->
            _userStories.postValue(stories)
        }
    }
}
