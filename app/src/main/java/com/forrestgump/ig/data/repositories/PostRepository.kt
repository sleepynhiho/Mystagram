package com.forrestgump.ig.data.repositories

import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.forrestgump.ig.data.models.Comment
import com.forrestgump.ig.data.models.Notification
import com.forrestgump.ig.data.models.NotificationType
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.data.models.User
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class PostRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    // Lưu lại document cuối của trang hiện tại
    private var lastVisible: DocumentSnapshot? = null

    suspend fun getPosts(pageSize: Int): List<Post> = suspendCancellableCoroutine { cont ->
        var query: Query = firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(pageSize.toLong())
        // Nếu đã có trang trước thì bắt đầu sau document cuối của trang trước
        if (lastVisible != null) {
            query = query.startAfter(lastVisible!!)
        }
        query.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null && !snapshot.isEmpty) {
                    // Lưu lại document cuối cùng của trang hiện tại
                    lastVisible = snapshot.documents.last()
                    val posts = snapshot.toObjects(Post::class.java)
                    cont.resume(posts)
                } else {
                    cont.resume(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                cont.resumeWithException(exception)
            }
    }

    // Hàm reset lại pagination (để refresh load lại trang đầu)
    fun resetPagination() {
        lastVisible = null
    }

    @OptIn(UnstableApi::class)
    suspend fun addComment(post: Post, comment: Comment, currentUser: User) {
        try {
            // Lưu comment vào Firestore
            val commentRef = firestore.collection("posts")
                .document(post.postId)
                .collection("comments")
                .document()
            commentRef.set(comment, SetOptions.merge()).await()

            // Tăng số lượng comment cho bài viết
            updateCommentCount(post.postId)
            if (currentUser.userId != post.userId) {
                sendNotificationForComment(post, currentUser)
            }
        } catch (e: Exception) {
            Log.e("PostRepository", "Error adding comment: ${e.message}")
        }
    }

    @OptIn(UnstableApi::class)
    suspend fun updateCommentCount(postId: String) {
        try {
            val postRef = firestore.collection("posts").document(postId)

            firestore.runTransaction { transaction ->
                val postSnapshot = transaction.get(postRef)

                // Lấy số lượng comment hiện tại và tăng thêm 1
                val currentCommentCount = postSnapshot.getLong("commentsCount")?.toInt() ?: 0
                val newCommentCount = currentCommentCount + 1

                // Cập nhật lại commentsCount trong bài viết
                transaction.update(postRef, "commentsCount", newCommentCount)


            }.await()
        } catch (e: Exception) {
            Log.e("PostRepository", "Error updating comment count: ${e.message}")
        }
    }

    @OptIn(UnstableApi::class)
    private fun sendNotificationForComment(post: Post, currentUser: User) {
        try {
            // Lấy thông tin người chủ bài viết từ Firestore
            val postOwnerId = post.userId

            postOwnerId.let { ownerId ->
                // Tạo notification cho người chủ bài viết
                val notification = Notification(
                    notificationId = UUID.randomUUID().toString(),
                    receiverId = ownerId,  // Người chủ bài viết
                    senderId = currentUser.userId,  // Người gửi bình luận
                    senderUsername = currentUser.username,
                    senderProfileImage = currentUser.profileImage,
                    postId = post.postId,
                    isRead = false,
                    type = NotificationType.COMMENT,
                    timestamp = null
                )

                // Lưu notification vào Firestore
                firestore.collection("notifications").document(notification.notificationId)
                    .set(notification)
            }
        } catch (e: Exception) {
            Log.e("Notification", "Error sending notification for comment: ${e.message}")
        }
    }

    // Lấy comment từ Firestore
    fun getComments(postId: String): Flow<List<Comment>> = callbackFlow {
        val listenerRegistration = firestore.collection("posts")
            .document(postId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val comments = snapshot?.documents?.mapNotNull { it.toObject(Comment::class.java) }
                    ?: emptyList()
                trySend(comments)
            }

        awaitClose { listenerRegistration.remove() }
    }

    fun observePosts(onPostsUpdated: (List<Post>) -> Unit) {
        firestore.collection("posts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                snapshot?.documents?.let { documents ->
                    val posts = documents.mapNotNull { it.toObject(Post::class.java) }
                    onPostsUpdated(posts)
                }
            }
    }


}