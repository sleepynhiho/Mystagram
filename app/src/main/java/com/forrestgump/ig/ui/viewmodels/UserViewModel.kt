package com.forrestgump.ig.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.forrestgump.ig.R
import com.forrestgump.ig.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
        if (firebaseUser != null) {
            val db = FirebaseFirestore.getInstance()
            val uid = firebaseUser.uid

            db.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Convert Firestore document to User object
                        val userData = document.toObject(User::class.java)
                        _user.value = userData
                        Log.d("UserViewModel", "document exists")
                    } else {
                        // User document doesn't exist in Firestore, create a new User object from Firebase Auth
                        _user.value = User(
                            userId = uid,
                            username = firebaseUser.displayName ?: "Unknown",
                            fullName = firebaseUser.displayName ?: "User",
                            email = firebaseUser.email ?: "",
                            profileImage = firebaseUser.photoUrl?.toString() ?: R.drawable.default_profile_img.toString(),
                            bio = "",
                            followers = emptyList(),
                            following = emptyList()
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle the error here
                    println("Error getting user document: $exception")
                }
        }
    }
}
