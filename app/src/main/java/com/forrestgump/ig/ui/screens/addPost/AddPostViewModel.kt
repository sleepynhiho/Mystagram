package com.forrestgump.ig.ui.screens.addPost

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.forrestgump.ig.BuildConfig
import com.forrestgump.ig.data.models.Post
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val cloudinary: Cloudinary
) : ViewModel() {
    var uiState = MutableStateFlow(UiState())
        private set
    var selectedImages = MutableStateFlow<List<Uri>>(emptyList())
        private set

    // Hàm cập nhật danh sách ảnh
    fun updateSelectedImages(newImages: List<Uri>) {
        selectedImages.value = newImages
    }

    private fun clearUiState() {
        uiState.update { UiState() }
    }

    override fun onCleared() {
        super.onCleared()
        clearUiState()
    }

    private suspend fun uploadImageToCloudinary(context: Context, fileUri: Uri): String =
        withContext(Dispatchers.IO) {
            try {
                val file = getFileFromUri(context, fileUri)
                Log.d("AddPostViewModel", "${file}")
                // Sử dụng cloudinary đã được inject
                val result = cloudinary.uploader().unsignedUpload(
                    file,
                    BuildConfig.CLOUDINARY_UPLOAD_PRESET,
                    emptyMap<String, Any>()
                )
                result["secure_url"] as String
            } catch (e: Exception) {
                throw e
            }
        }
    fun uploadPostToFirebase(
        context: Context,
        caption: String,
        userId: String,
        username: String,
        profileImageUrl: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val images = selectedImages.value
                if (images.isEmpty()) {
                    throw Exception("Chưa có ảnh để upload")
                }
                val mediaUrls = mutableListOf<String>()
                // Upload từng ảnh theo thứ tự
                images.forEach { uri ->
                    val imageUrl = uploadImageToCloudinary(context, uri)
                    mediaUrls.add(imageUrl)
                }
                // Tạo post với thông tin từ Firebase
                val postId = firestore.collection("posts").document().id
                val post = Post(
                    postId = postId,
                    userId = userId,
                    username = username,
                    profileImageUrl = profileImageUrl,
                    mediaUrls = mediaUrls,
                    caption = caption,
                    reactions = emptyMap(),
                    commentsCount = 0,
                    mimeType = "image",
                    timestamp = null // @ServerTimestamp sẽ được Firestore set tự động
                )
                // Sử dụng suspend function nếu bạn dùng extension cho Task (hoặc dùng await với Tasks)
                firestore.collection("posts").document(postId).set(post)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception)
                    }
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun updateReaction(postId: String, currentUserId: String, previousReaction: String?, newReaction: String?) {
        viewModelScope.launch {
            try {
                val postRef = firestore.collection("posts").document(postId)

                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(postRef)

                    // Lấy dữ liệu reactions từ Firestore
                    val reactions = snapshot["reactions"] as? Map<String, List<String>> ?: emptyMap()
                    val updatedReactions = reactions.toMutableMap()

                    // Xóa reaction cũ (nếu có)
                    previousReaction?.let { oldReaction ->
                        val users = updatedReactions[oldReaction]?.toMutableList() ?: mutableListOf()
                        users.remove(currentUserId)
                        if (users.isEmpty()) updatedReactions.remove(oldReaction) else updatedReactions[oldReaction] = users
                    }

                    // Thêm reaction mới (nếu có)
                    newReaction?.let { newReact ->
                        val users = updatedReactions[newReact]?.toMutableList() ?: mutableListOf()
                        if (!users.contains(currentUserId)) {
                            users.add(currentUserId)
                            updatedReactions[newReact] = users
                        }
                    }

                    transaction.update(postRef, "reactions", updatedReactions)
                }
            } catch (e: Exception) {
                Log.e("UpdateReaction", "Error updating reaction", e)
            }
        }
    }

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post


    fun observePost(postId: String) {
        firestore.collection("posts")
            .document(postId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("PostViewModel", "Error fetching post data", error)
                    return@addSnapshotListener
                }

                snapshot?.toObject(Post::class.java)?.let { updatedPost ->
                    _post.value = updatedPost
                }
            }
    }



}
