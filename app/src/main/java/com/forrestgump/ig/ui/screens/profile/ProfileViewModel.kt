package com.forrestgump.ig.ui.screens.profile

import android.content.Context
import android.net.Uri
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
import com.google.firebase.firestore.WriteBatch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Date
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
                            profileImage = document.getString("profileImage") ?: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSlRM2-AldpZgaraCXCnO5loktGi0wGiNPydQ&s",
                            bio = document.getString("bio") ?: "No Bio yet",
                            location = document.getString("location") ?: "",
                            // followers và following được lưu là List<String> trong document
                            followers = document.get("followers") as? List<String> ?: emptyList(),
                            following = document.get("following") as? List<String> ?: emptyList(),
                            isPrivate = document.getBoolean("private") ?: false,
                            isPremium = document.getBoolean("premium") ?: false,
                            premiumDate = document.getTimestamp("premiumDate")?.toDate()

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
        val usernameChanged = currentUser.username != newUsername
        val profileImageChanged = currentUser.profileImage != newProfileImage
        
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
                    // Chỉ cập nhật các bảng liên quan nếu username hoặc profileImage thay đổi
                    if (usernameChanged || profileImageChanged) {
                        updateUserDataAcrossCollections(
                            userId = currentUser.userId,
                            newUsername = newUsername, 
                            newProfileImage = imageUrl,
                            usernameChanged = usernameChanged,
                            profileImageChanged = profileImageChanged,
                            onSuccess = onSuccess,
                            onFailure = onFailure
                        )
                    } else {
                                onSuccess()
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

    /**
     * Cập nhật thông tin người dùng trên tất cả các bảng dữ liệu liên quan
     */
    private fun updateUserDataAcrossCollections(
        userId: String,
        newUsername: String,
        newProfileImage: String,
        usernameChanged: Boolean,
        profileImageChanged: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val batch = firestore.batch()
                val batches = mutableListOf<WriteBatch>()
                var currentBatch = firestore.batch()
                var operationCount = 0
                val MAX_BATCH_SIZE = 450 // Firestore giới hạn 500 operations/batch
                
                Log.d("ProfileViewModel", "Starting to update user data across collections")
                
                // 1. Cập nhật bài đăng (Posts)
                if (usernameChanged || profileImageChanged) {
                    val postsQuery = firestore.collection("posts")
                        .whereEqualTo("userId", userId)
                        .get()
                        .await()
                    
                    Log.d("ProfileViewModel", "Found ${postsQuery.size()} posts to update")
                    
                    for (postDoc in postsQuery.documents) {
                        val updates = mutableMapOf<String, Any>()
                        if (usernameChanged) updates["username"] = newUsername
                        if (profileImageChanged) updates["profileImageUrl"] = newProfileImage
                        
                        currentBatch.update(postDoc.reference, updates)
                        operationCount++
                        
                        if (operationCount >= MAX_BATCH_SIZE) {
                            batches.add(currentBatch)
                            currentBatch = firestore.batch()
                            operationCount = 0
                        }
                    }
                }
                
                // 2. Cập nhật bình luận (Comments)
                if (usernameChanged || profileImageChanged) {
                    // Lấy tất cả bài posts (không chỉ của user hiện tại)
                    val allPostsQuery = firestore.collection("posts").get().await()
                    
                    for (postDoc in allPostsQuery.documents) {
                        val commentsQuery = firestore.collection("posts")
                            .document(postDoc.id)
                            .collection("comments")
                            .whereEqualTo("userId", userId)
                            .get()
                            .await()
                        
                        Log.d("ProfileViewModel", "Found ${commentsQuery.size()} comments to update in post ${postDoc.id}")
                        
                        for (commentDoc in commentsQuery.documents) {
                            val updates = mutableMapOf<String, Any>()
                            if (usernameChanged) updates["username"] = newUsername
                            if (profileImageChanged) updates["profileImage"] = newProfileImage
                            
                            currentBatch.update(commentDoc.reference, updates)
                            operationCount++
                            
                            if (operationCount >= MAX_BATCH_SIZE) {
                                batches.add(currentBatch)
                                currentBatch = firestore.batch()
                                operationCount = 0
                            }
                        }
                    }
                }
                
                // 3. Cập nhật thông báo (Notifications)
                if (usernameChanged || profileImageChanged) {
                    // Cập nhật notifications mà user gửi đi
                    val sentNotificationsQuery = firestore.collection("notifications")
                        .whereEqualTo("senderId", userId)
                        .get()
                        .await()
                    
                    Log.d("ProfileViewModel", "Found ${sentNotificationsQuery.size()} sent notifications to update")
                    
                    for (notifDoc in sentNotificationsQuery.documents) {
                        val updates = mutableMapOf<String, Any>()
                        if (usernameChanged) updates["senderUsername"] = newUsername
                        if (profileImageChanged) updates["senderProfileImage"] = newProfileImage
                        
                        currentBatch.update(notifDoc.reference, updates)
                        operationCount++
                        
                        if (operationCount >= MAX_BATCH_SIZE) {
                            batches.add(currentBatch)
                            currentBatch = firestore.batch()
                            operationCount = 0
                        }
                    }
                }
                
                // 4. Cập nhật chat
                if (usernameChanged || profileImageChanged) {
                    // Chats nơi user là user1
                    val chatsAsUser1Query = firestore.collection("chats")
                        .whereEqualTo("user1Id", userId)
                        .get()
                        .await()
                    
                    Log.d("ProfileViewModel", "Found ${chatsAsUser1Query.size()} chats as user1 to update")
                    
                    for (chatDoc in chatsAsUser1Query.documents) {
                        val updates = mutableMapOf<String, Any>()
                        if (usernameChanged) updates["user1Username"] = newUsername
                        if (profileImageChanged) updates["user1ProfileImage"] = newProfileImage
                        
                        currentBatch.update(chatDoc.reference, updates)
                        operationCount++
                        
                        if (operationCount >= MAX_BATCH_SIZE) {
                            batches.add(currentBatch)
                            currentBatch = firestore.batch()
                            operationCount = 0
                        }
                    }
                    
                    // Chats nơi user là user2
                    val chatsAsUser2Query = firestore.collection("chats")
                        .whereEqualTo("user2Id", userId)
                        .get()
                        .await()
                    
                    Log.d("ProfileViewModel", "Found ${chatsAsUser2Query.size()} chats as user2 to update")
                    
                    for (chatDoc in chatsAsUser2Query.documents) {
                        val updates = mutableMapOf<String, Any>()
                        if (usernameChanged) updates["user2Username"] = newUsername
                        if (profileImageChanged) updates["user2ProfileImage"] = newProfileImage
                        
                        currentBatch.update(chatDoc.reference, updates)
                        operationCount++
                        
                        if (operationCount >= MAX_BATCH_SIZE) {
                            batches.add(currentBatch)
                            currentBatch = firestore.batch()
                            operationCount = 0
                        }
                    }
                }
                
                // 5. Cập nhật stories
                if (usernameChanged || profileImageChanged) {
                    // Cập nhật trong collection stories (các story đơn lẻ)
                    val storiesQuery = firestore.collection("stories")
                        .whereEqualTo("userId", userId)
                        .get()
                        .await()
                    
                    Log.d("ProfileViewModel", "Found ${storiesQuery.size()} individual stories to update")
                    
                    for (storyDoc in storiesQuery.documents) {
                        val updates = mutableMapOf<String, Any>()
                        if (usernameChanged) updates["username"] = newUsername
                        if (profileImageChanged) updates["profileImage"] = newProfileImage
                        
                        currentBatch.update(storyDoc.reference, updates)
                        operationCount++
                        
                        if (operationCount >= MAX_BATCH_SIZE) {
                            batches.add(currentBatch)
                            currentBatch = firestore.batch()
                            operationCount = 0
                        }
                    }
                    
                    // Cập nhật trong collection userStories (tập hợp stories của user)
                    val userStoriesQuery = firestore.collection("userStories")
                        .whereEqualTo("userId", userId)
                        .get()
                        .await()
                    
                    Log.d("ProfileViewModel", "Found ${userStoriesQuery.size()} userStory entries to update")
                    
                    for (userStoryDoc in userStoriesQuery.documents) {
                        val updates = mutableMapOf<String, Any>()
                        if (usernameChanged) updates["username"] = newUsername

                        
                        currentBatch.update(userStoryDoc.reference, updates)
                        operationCount++
                        
                        if (operationCount >= MAX_BATCH_SIZE) {
                            batches.add(currentBatch)
                            currentBatch = firestore.batch()
                            operationCount = 0
                        }
                    }
                }
                
                // Thêm batch cuối cùng nếu còn operations
                if (operationCount > 0) {
                    batches.add(currentBatch)
                }
                
                // Commit tất cả các batches
                Log.d("ProfileViewModel", "Committing ${batches.size} batches")
                
                for (batchToCommit in batches) {
                    batchToCommit.commit().await()
                }
                
                Log.d("ProfileViewModel", "Successfully updated all user data across collections")
                onSuccess()
                
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating user data across collections", e)
                onFailure(e)
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
        // Save to Firebase immediately
        firestore.collection("users").document(currentUser.userId)
            .update("location", newLocation)
            .addOnSuccessListener {
                Log.d("ProfileViewModel", "Location updated in Firebase: $newLocation")
            }
            .addOnFailureListener { e ->
                Log.e("ProfileViewModel", "Error updating location", e)
            }

        Log.d("ProfileViewModel", "updateLocalUserLocation: ${uiState.value.curUser.location}")
    }

    // Add this method to ProfileViewModel class
    fun updatePremiumStatus(
        isPremium: Boolean,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val currentUser = uiState.value.curUser
        val premiumDate = if (isPremium) Date() else null

        // Update local state
        val updatedUser = currentUser.copy(
            isPremium = isPremium,
            premiumDate = premiumDate
        )
        uiState.update { it.copy(curUser = updatedUser) }

        // Update Firestore
        val updates = mapOf(
            "premium" to isPremium,
            "premiumDate" to premiumDate
        )

        firestore.collection("users").document(currentUser.userId)
            .update(updates as Map<String, Any?>)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Revert local state on failure
                uiState.update { it.copy(curUser = currentUser) }
                onFailure(exception)
            }
    }

    // Add this method to check and update premium status on app start
    fun checkPremiumExpiration() {
        val currentUser = uiState.value.curUser
        if (currentUser.isPremium && currentUser.isPremiumExpired()) {
            updatePremiumStatus(false)
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