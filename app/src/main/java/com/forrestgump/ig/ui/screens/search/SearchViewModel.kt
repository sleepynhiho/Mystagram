package com.forrestgump.ig.ui.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val cloudinary: Cloudinary,
) : ViewModel() {

    var uiState = MutableStateFlow(UiState())
        private set

    fun loadAllData() {
        viewModelScope.launch {
            uiState.update { it.copy(isLoading = true) }

            // Fetch all users
            firestore.collection("users").get()
                .addOnSuccessListener { querySnapshot ->
                    val users = querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(User::class.java)
                    }
                    uiState.update { it.copy(users = users) } // Ensure 'users' is part of UiState
                }
                .addOnFailureListener { exception ->
                    Log.e("SearchViewModel", "Error loading users", exception)
                }

            // Fetch all posts
            firestore.collection("posts").get()
                .addOnSuccessListener { querySnapshot ->
                    val posts = querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Post::class.java)
                    }
                    uiState.update { it.copy(posts = posts, isLoading = false) }
                }
                .addOnFailureListener { exception ->
                    Log.e("SearchViewModel", "Error loading posts", exception)
                    uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    private fun clearUiState() {
        uiState.update { UiState() }
    }

    override fun onCleared() {
        super.onCleared()
        clearUiState()
    }
}