package com.forrestgump.ig.ui.screens.profile

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.forrestgump.ig.BuildConfig
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore, // Inject Firestore từ AppModule
    private val cloudinary: Cloudinary,
) : ViewModel() {

    var uiState = MutableStateFlow(UiState())
        private set


    fun loadUserData() {
        val userFromFB = FirebaseAuth.getInstance().currentUser
        userFromFB?.let { user ->
            val userId = user.uid
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val updatedUser = User(
                            userId = userId,
                            fullName = document.getString("fullName") ?: "",
                            username = document.getString("username") ?: "",
                            profileImage = document.getString("profileImage") ?: "@drawable/default_profile_image",
                            bio = document.getString("bio") ?: "No Bio yet",
                            location = document.getString("location") ?: "",
                            // followers và following được lưu là List<String> trong document
                            followers = document.get("followers") as? List<String> ?: emptyList(),
                            following = document.get("following") as? List<String> ?: emptyList(),
//                            isPrivate = document.getBoolean("private") ?: false,
//                            isPremium = document.getBoolean("premium") ?: false
                        )
                        uiState.update { currentState ->
                            currentState.copy(isLoading = false, curUser = updatedUser)
                        }
                        // Sau đó, load thông tin posts của user
                        firestore.collection("posts")
                            .whereEqualTo("userId", userId)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                val posts = querySnapshot.documents.mapNotNull { doc ->
                                    doc.toObject(Post::class.java)
                                }
                                uiState.update { currentState ->
                                    currentState.copy(
                                        postCount = posts.size,
                                        posts = posts
                                    )
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e("ProfileViewModel", "Error loading posts", exception)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileViewModel", "Error getting user data", exception)
                    // Cập nhật lại state nếu cần thông báo lỗi...
                    uiState.update { currentState ->
                        currentState.copy(isLoading = false)
                    }
                }
        } ?: run {
            // Nếu user là null
            uiState.update { currentState ->
                currentState.copy(isLoading = false)
            }
        }
    }

    fun getPostById(postId: String): Post? {
        return uiState.value.posts.find { it.postId == postId }
    }

    private fun getFileFromUri(context: Context, uriString: String): File? {
        val uri = uriString.toUri()
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }


    /**
     * Kiểm tra xem chuỗi newProfileImage đã là URL hay chưa.
     * Nếu nó chưa chứa "http", coi như đó là đường dẫn file cần upload.
     */
    private fun isLocalImage(imagePath: String): Boolean {
        return !imagePath.startsWith("http://") && !imagePath.startsWith("https://")
    }

    fun updateUserProfile(
        context: Context,
        newProfileImage: String,
        newFullName: String,
        newUsername: String,
        newBio: String,
        newAccountPrivacy: Boolean,
        newLocation: String? = null, // Add location parameter
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val currentUser = uiState.value.curUser
        fun updateFirestoreWithImage(imageUrl: String) {
            val updatedUser = currentUser.copy(
                profileImage = imageUrl,
                fullName = newFullName,
                username = newUsername,
                isPrivate = newAccountPrivacy,
                bio = newBio,
                location = newLocation ?: currentUser.location // Update location if provided
            )
            // Cập nhật uiState ngay trên local
            uiState.update { it.copy(curUser = updatedUser) }


            // Create a map for updates
            val updates = mutableMapOf(
                "profileImage" to imageUrl,
                "fullName" to newFullName,
                "username" to newUsername,
                "bio" to newBio,
                "private" to newAccountPrivacy
            )

            // Add location to updates if provided
            if (newLocation != null) {
                updates["location"] = newLocation
            }
            Log.d("ProfileViewModel", "New Location: $newLocation")

            // Tham chiếu đến document của user
            val userDocRef = firestore.collection("users").document(currentUser.userId)
            userDocRef.update(updates as Map<String, Any>)
                .addOnSuccessListener {

                // Sau khi cập nhật user, tiến hành cập nhật ảnh đại diện trong các bài post
                firestore.collection("posts")
                    .whereEqualTo("userId", currentUser.userId)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val batch = firestore.batch()
                        querySnapshot.documents.forEach { doc ->
                            // Giả sử trường ảnh đại diện trong post là "profileImageUrl"
                            batch.update(doc.reference, "profileImageUrl", imageUrl)
                        }
                        batch.commit()
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener { exception ->
                                onFailure(exception)
                            }
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception)
                    }
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
        }

        viewModelScope.launch {
            if (isLocalImage(newProfileImage)) {
                try {
                    Log.d("ProfileViewModel", "${newProfileImage}")

                    // Nếu newProfileImage là content URI, chuyển thành file tạm
                    val fileToUpload = getFileFromUri(context, newProfileImage)

                    Log.d("ProfileViewModel", "${fileToUpload}")
                    val uploadResult = withContext(Dispatchers.IO) {
                        cloudinary.uploader().unsignedUpload(
                            fileToUpload,
                            BuildConfig.CLOUDINARY_UPLOAD_PRESET,
                            emptyMap<String, Any>())
                    }
                    val uploadedImageUrl = uploadResult["secure_url"] as? String
                    if (uploadedImageUrl != null) {
                        updateFirestoreWithImage(uploadedImageUrl)
                    } else {
                        onFailure(Exception("Upload thất bại: không có URL trả về"))
                    }
                } catch (e: Exception) {
                    onFailure(e)
                }
            } else {
                updateFirestoreWithImage(newProfileImage)
            }
        }

    }

    fun updateLocalUserLocation(newLocation: String) {
        val currentUser = uiState.value.curUser
        val updatedUser = currentUser.copy(
            location = newLocation
        )
        // Update local state only
        uiState.update { it.copy(curUser = updatedUser) }
        Log.d("ProfileViewModel", "updateLocalUserLocation: ${uiState.value.curUser.location}")
    }

    private fun clearUiState() {
        uiState.update { UiState() }
    }

    override fun onCleared() {
        super.onCleared()
        clearUiState()
    }
}