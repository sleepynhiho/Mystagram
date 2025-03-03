package com.forrestgump.ig.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

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
                                    onResult(true, null)
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
