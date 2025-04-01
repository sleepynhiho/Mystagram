package com.forrestgump.ig.ui.screens.auth

import android.app.Activity
import android.content.Context
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forrestgump.ig.R
import com.forrestgump.ig.data.models.User
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ViewModel() {

    // Khởi tạo FirebaseAuth instance
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

    // Hàm đăng nhập
    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, task.exception?.message)
                    }
                }
        }
    }

    // Hàm đăng ký
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
                        // Sau khi đăng ký thành công, cập nhật displayName của người dùng
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
                                        profileImage = "@drawable/default_profile_image",
                                    )
                                    firestore.collection("users").document(user.uid).set(userData)
                                        .addOnSuccessListener {
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

    // Hàm quên mật khẩu
    fun forgotPassword(context: Context, email: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    onResult(true, null)
                                } else {
                                    onResult(false, task.exception?.message)
                                }
                            }
                    } else {
                        onResult(false, context.getString(R.string.email_has_not_been_registered))
                    }
                }
                .addOnFailureListener { e ->
                    onResult(
                        false,
                        e.message ?: context.getString(R.string.error_when_checking_email)
                    )
                }
        }
    }


}
