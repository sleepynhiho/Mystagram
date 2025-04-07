package com.forrestgump.ig.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forrestgump.ig.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var uiState = MutableStateFlow(UiState())
        private set

    fun updateEmail(newEmail: String) {
        uiState.value = uiState.value.copy(email = newEmail)
    }

    fun updateUsername(newUsername: String) {
        uiState.value = uiState.value.copy(username = newUsername)
    }

    fun updatePassword(newPassword: String) {
        uiState.value = uiState.value.copy(password = newPassword)
    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.uid?.let { userId ->
                            saveFCMTokenToFirestore(userId)
                        }
                        onResult(true, null)
                    } else {
                        onResult(false, task.exception?.message)
                    }
                }
        }
    }

    fun signup(
        email: String,
        password: String,
        username: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()
                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    val userData = User(
                                        userId = user.uid,
                                        email = email,
                                        username = username,
                                        profileImage = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSlRM2-AldpZgaraCXCnO5loktGi0wGiNPydQ&s",
                                    )
                                    firestore.collection("users").document(user.uid).set(userData)
                                        .addOnSuccessListener {
                                            saveFCMTokenToFirestore(user.uid)
                                            onResult(true, null)
                                        }
                                        .addOnFailureListener { e ->
                                            onResult(false, e.message)
                                         }
                                } else {
                                    onResult(false, updateTask.exception?.message)
                                }
                            }
                    } else {
                        onResult(false, task.exception?.message)
                    }
                }
        }
    }

    private fun saveFCMTokenToFirestore(userId: String) {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) return@addOnCompleteListener
                val token = task.result

                FirebaseFirestore.getInstance().collection("users")
                    .document(userId)
                    .update("fcmToken", token)
                    .addOnSuccessListener { println("FCM Token saved for $userId") }
                    .addOnFailureListener { e -> println("Error saving FCM Token: $e") }
            }
    }

    fun forgotPassword(email: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, task.exception?.message)
                    }
                }
        }
    }
}
