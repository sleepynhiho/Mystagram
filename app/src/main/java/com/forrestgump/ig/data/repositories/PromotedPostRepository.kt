package com.forrestgump.ig.data.repositories

import android.util.Log
import com.forrestgump.ig.data.models.PromotedPost
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromotedPostRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    // Promote a post
    suspend fun promotePost(postId: String, userId: String): Boolean {
        return try {
            val promotedPost = PromotedPost(
                postId = postId,
                userId = userId,
                promotionStartDate = Date(),
                promotionEndDate = null // For now, promotions don't expire
            )

            firestore.collection("promotedPosts")
                .document(postId)
                .set(promotedPost)
                .await()

            true
        } catch (e: Exception) {
            Log.e("PromotedPostRepository", "Error promoting post: ${e.message}")
            false
        }
    }

    // Get all promoted posts
    suspend fun getPromotedPosts(): List<PromotedPost> {
        return try {
            val snapshot = firestore.collection("promotedPosts")
                .get()
                .await()

            snapshot.toObjects(PromotedPost::class.java)
        } catch (e: Exception) {
            Log.e("PromotedPostRepository", "Error getting promoted posts: ${e.message}")
            emptyList()
        }
    }

    // Check if a post is promoted
    suspend fun isPostPromoted(postId: String): Boolean {
        return try {
            val doc = firestore.collection("promotedPosts")
                .document(postId)
                .get()
                .await()

            doc.exists()
        } catch (e: Exception) {
            Log.e("PromotedPostRepository", "Error checking if post is promoted: ${e.message}")
            false
        }
    }

    // Remove post from promotion
    suspend fun removePromotion(postId: String): Boolean {
        return try {
            firestore.collection("promotedPosts")
                .document(postId)
                .delete()
                .await()

            true
        } catch (e: Exception) {
            Log.e("PromotedPostRepository", "Error removing promotion: ${e.message}")
            false
        }
    }
}