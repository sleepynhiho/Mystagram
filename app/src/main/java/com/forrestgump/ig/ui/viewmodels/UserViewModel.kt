package com.forrestgump.ig.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.forrestgump.ig.R
import com.forrestgump.ig.data.models.User
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor() : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    init {
        fetchCurrentUser()
    }

    private fun fetchCurrentUser() {
        val firebaseUser = firebaseAuth.currentUser
        _user.value = firebaseUser?.let {
            User(
                userId = it.uid,
                username = it.displayName ?: "Unknown",
                fullName = it.displayName ?: "User",
                email = it.email ?: "",
                profileImage = it.photoUrl?.toString() ?: R.drawable.default_profile_img.toString(),
                bio = "",
                followers = emptyList(),
                following = emptyList()
            )
        }
    }
}
