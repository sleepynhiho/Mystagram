package com.forrestgump.ig.data.repositories

import com.forrestgump.ig.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendSuggestionRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    // Get suggestions based on mutual followers
    suspend fun getSuggestionsBasedOnMutualFollowers(
        currentUserId: String,
        limit: Int = 10
    ): List<SuggestionWithScore> {
        try {
            // Get current user data
            val currentUserDoc = firestore.collection("users")
                .document(currentUserId)
                .get()
                .await()
            
            val currentUser = currentUserDoc.toObject(User::class.java) ?: return emptyList()
            
            // Get all users
            val allUsersSnapshot = firestore.collection("users")
                .whereNotEqualTo("userId", currentUserId) // Exclude current user
                .get()
                .await()
            
            val allUsers = allUsersSnapshot.documents.mapNotNull { it.toObject(User::class.java) }
            
            // Filter out users that the current user is already following
            val potentialSuggestions = allUsers.filter { user ->
                !currentUser.following.contains(user.userId)
            }
            
            // Calculate mutual followers for each potential suggestion
            val suggestionsWithScores = potentialSuggestions.map { user ->
                val mutualFollowers = currentUser.followers.intersect(user.followers.toSet())
                val mutualFollowing = currentUser.following.intersect(user.following.toSet())
                val totalMutual = mutualFollowers.size + mutualFollowing.size
                
                SuggestionWithScore(
                    user = user,
                    score = totalMutual,
                    mutualConnections = mutualFollowers.size + mutualFollowing.size,
                    reason = if (totalMutual > 0) "Has $totalMutual mutual connection(s)" else "Suggested for you"
                )
            }
            
            // Sort by score (number of mutual connections) and take top results
            return suggestionsWithScores
                .sortedByDescending { it.score }
                .take(limit)
            
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }
    
    // Get suggestions based on post interactions
    suspend fun getSuggestionsBasedOnPostInteractions(
        currentUserId: String,
        limit: Int = 10
    ): List<SuggestionWithScore> {
        try {
            // Get current user
            val currentUserDoc = firestore.collection("users")
                .document(currentUserId)
                .get()
                .await()
            
            val currentUser = currentUserDoc.toObject(User::class.java) ?: return emptyList()
            
            // Get all posts that the current user has interacted with (reactions)
            val postsSnapshot = firestore.collection("posts")
                .get()
                .await()
                
            val posts = postsSnapshot.documents.mapNotNull { it.toObject(com.forrestgump.ig.data.models.Post::class.java) }
            
            // Find posts the current user has interacted with
            val interactedPosts = posts.filter { post ->
                post.reactions.values.any { userIds -> userIds.contains(currentUserId) }
            }
            
            // Get all users
            val allUsersSnapshot = firestore.collection("users")
                .whereNotEqualTo("userId", currentUserId) // Exclude current user
                .get()
                .await()
                
            val allUsers = allUsersSnapshot.documents.mapNotNull { it.toObject(User::class.java) }
            
            // Filter out users that the current user is already following
            val potentialSuggestions = allUsers.filter { user ->
                !currentUser.following.contains(user.userId)
            }
            
            // Create a map to count how many of the same posts each user has interacted with
            val userInteractionCounts = mutableMapOf<String, Int>()
            
            interactedPosts.forEach { post ->
                // Get all users who reacted to this post
                val userIdsWhoReacted = post.reactions.values.flatten().toSet()
                
                // For each potential suggestion user who also reacted to this post
                potentialSuggestions.forEach { user ->
                    if (userIdsWhoReacted.contains(user.userId)) {
                        userInteractionCounts[user.userId] = (userInteractionCounts[user.userId] ?: 0) + 1
                    }
                }
            }
            
            // Create suggestions with scores
            val suggestionsWithScores = potentialSuggestions.map { user ->
                val interactionCount = userInteractionCounts[user.userId] ?: 0
                
                SuggestionWithScore(
                    user = user,
                    score = interactionCount,
                    mutualConnections = 0, // Not relevant for this type of suggestion
                    reason = if (interactionCount > 0) 
                        "Interacted with ${interactionCount} of the same posts" 
                        else "Suggested for you"
                )
            }
            
            // Sort by score and take top results
            return suggestionsWithScores
                .sortedByDescending { it.score }
                .take(limit)
                
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }
    
    // Combine all suggestion types and return the best ones
    suspend fun getCombinedSuggestions(
        currentUserId: String,
        limit: Int = 10
    ): List<SuggestionWithScore> {
        val mutualSuggestions = getSuggestionsBasedOnMutualFollowers(currentUserId)
        val interactionSuggestions = getSuggestionsBasedOnPostInteractions(currentUserId)
        
        // Combine suggestions
        val combinedMap = mutableMapOf<String, SuggestionWithScore>()
        
        // Add mutual suggestions first
        mutualSuggestions.forEach { suggestion ->
            combinedMap[suggestion.user.userId] = suggestion
        }
        
        // Add or update with interaction suggestions
        interactionSuggestions.forEach { suggestion ->
            val existingSuggestion = combinedMap[suggestion.user.userId]
            
            if (existingSuggestion != null) {
                // If this user is already suggested, combine the scores and reasons
                combinedMap[suggestion.user.userId] = existingSuggestion.copy(
                    score = existingSuggestion.score + suggestion.score,
                    reason = if (existingSuggestion.reason != "Suggested for you" && suggestion.reason != "Suggested for you") {
                        "${existingSuggestion.reason} and ${suggestion.reason.lowercase()}"
                    } else if (existingSuggestion.reason != "Suggested for you") {
                        existingSuggestion.reason
                    } else {
                        suggestion.reason
                    }
                )
            } else {
                combinedMap[suggestion.user.userId] = suggestion
            }
        }
        
        // Convert to list, sort by score, and take top suggestions
        return combinedMap.values.toList()
            .sortedByDescending { it.score }
            .take(limit)
    }
}

// Data class for suggestions with scores and reasons
data class SuggestionWithScore(
    val user: User,
    val score: Int,
    val mutualConnections: Int,
    val reason: String
) 