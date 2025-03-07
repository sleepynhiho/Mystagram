package com.forrestgump.ig.data.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cloudinary.Cloudinary
import com.forrestgump.ig.R
import com.forrestgump.ig.data.models.Story
import com.forrestgump.ig.data.models.UserStory
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class StoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val cloudinary: Cloudinary
) {

    suspend fun uploadStoryImage(imageUri: Uri, username: String, context: Context): Result<String> {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: return Result.failure(Exception("Không thể đọc ảnh"))

            val tempFile = withContext(Dispatchers.IO) {
                File.createTempFile("story_", ".jpg", context.cacheDir)
            }.apply {
                outputStream().use { inputStream.copyTo(it) }
            }

            // Upload file tạm thời lên Cloudinary
            val uploadResult = cloudinary.uploader().upload(tempFile, mapOf("folder" to "stories"))
            val imageUrl = uploadResult["url"] as String

            // Lưu vào Firestore
            val storyId = firestore.collection("stories").document().id
            val story = Story(
                storyId = storyId,
                username = username,
                media = imageUrl,
                mimeType = "image"
            )

            firestore.collection("stories").document(storyId).set(story).await()

            Result.success(imageUrl)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserStories(callback: (List<UserStory>) -> Unit) {
        firestore.collection("stories")
            .get()
            .addOnSuccessListener { userDocuments ->
                val userStoriesList = mutableListOf<UserStory>()

                val tasks = userDocuments.map { userDoc ->
                    val userId = userDoc.id
                    val username = userDoc.getString("username") ?: "Unknown"
                    val profileImage = userDoc.getString("profileImage") ?: R.drawable.default_profile_img.toString()

                    firestore.collection("stories").document(userId)
                        .collection("stories")
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .get()
                        .continueWith { storyTask ->
                            val storyList = storyTask.result?.documents?.mapNotNull { doc ->
                                doc.toObject(Story::class.java)?.copy(storyId = doc.id)
                            } ?: emptyList()

                            userStoriesList.add(
                                UserStory(
                                    userId = userId,
                                    username = username,
                                    profileImage = profileImage,
                                    stories = storyList
                                )
                            )
                        }
                }

                Tasks.whenAllSuccess<Any>(tasks).addOnCompleteListener {
                    callback(userStoriesList)
                }
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }


}
