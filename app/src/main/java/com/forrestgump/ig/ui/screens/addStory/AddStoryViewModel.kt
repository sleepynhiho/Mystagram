package com.forrestgump.ig.ui.screens.addStory

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.forrestgump.ig.R
import com.forrestgump.ig.data.models.Story
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.data.models.UserStory
import com.forrestgump.ig.ui.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val cloudinary: Cloudinary,
) : ViewModel() {

    fun uploadStoryImage(
        user: User?,
        imageUri: Uri,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (user == null) {
                    withContext(Dispatchers.Main) {
                        onError("Người dùng chưa đăng nhập")
                    }
                    return@launch
                }

                val userId = user.userId
                val username = user.username
                val profileImage = user.profileImage

                val inputStream = context.contentResolver.openInputStream(imageUri)
                    ?: throw IllegalArgumentException("Không thể đọc ảnh")

                val tempFile = File.createTempFile("story_", ".jpg", context.cacheDir).apply {
                    outputStream().use { inputStream.copyTo(it) }
                }

                val uploadResult =
                    cloudinary.uploader().upload(tempFile, mapOf("folder" to "stories"))
                val imageUrl = uploadResult["url"] as String

                val userDocRef = firestore.collection("stories").document(userId)
                val storyId = userDocRef.collection("stories").document().id

                val story = Story(
                    storyId = storyId,
                    username = username,
                    media = imageUrl,
                    mimeType = "image"
                )

                firestore.runTransaction { transaction ->
                    val userDoc = transaction.get(userDocRef)

                    if (!userDoc.exists()) {
                        val newUserStory = UserStory(
                            userId = userId,
                            username = username,
                            profileImage = profileImage,
                            stories = emptyList()
                        )
                        transaction.set(userDocRef, newUserStory)
                    }

                    // Thêm story vào subcollection
                    val storyDocRef = userDocRef.collection("stories").document(storyId)
                    transaction.set(storyDocRef, story)
                }.await()

                withContext(Dispatchers.Main) {
                    onSuccess()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e.localizedMessage ?: "Lỗi khi tải ảnh")
                }
            }
        }
    }
}
