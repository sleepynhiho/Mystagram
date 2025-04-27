package com.forrestgump.ig.data.repositories

import com.forrestgump.ig.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log

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
    
    // Get suggestions based on location proximity
    suspend fun getSuggestionsBasedOnLocation(
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
            
            // Skip if user doesn't have a location set
            if (currentUser.location.isNullOrBlank()) {
                Log.d("FriendRepo", "Current user has no location set, skipping location-based suggestions")
                return emptyList()
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
            
            // Create a location-based score for each user
            val suggestionsWithScores = potentialSuggestions.mapNotNull { user ->
                // Skip users without location
                if (user.location.isNullOrBlank()) return@mapNotNull null
                
                // Get location match score
                val locationMatchScore = calculateLocationMatchScore(currentUser.location, user.location)
                
                // Chỉ đề xuất người dùng có vị trí giống hoặc gần giống
                if (locationMatchScore < 50) {
                    // Điểm dưới 50 nghĩa là vị trí khác biệt nhiều, bỏ qua
                    return@mapNotNull null
                }
                
                SuggestionWithScore(
                    user = user,
                    score = locationMatchScore,
                    mutualConnections = 0, // Not relevant for this type of suggestion
                    reason = when {
                        locationMatchScore > 80 -> "Lives in the same area as you"
                        locationMatchScore >= 50 -> "From a nearby location"
                        else -> "From ${user.location}" // Không đến được đây vì đã lọc score < 50
                    }
                )
            }
            
            // Sort by score and take top results
            return suggestionsWithScores
                .sortedByDescending { it.score }
                .take(limit)
            
        } catch (e: Exception) {
            Log.e("FriendRepo", "Error in location-based suggestions", e)
            e.printStackTrace()
            return emptyList()
        }
    }
    
    // Calculate a match score between two locations
    private fun calculateLocationMatchScore(location1: String?, location2: String?): Int {
        if (location1.isNullOrBlank() || location2.isNullOrBlank()) return 0
        
        // Exact match
        if (location1.equals(location2, ignoreCase = true)) {
            return 100
        }
        
        // Check if one location contains the other
        if (location1.contains(location2, ignoreCase = true) || 
            location2.contains(location1, ignoreCase = true)) {
            return 80
        }
        
        // Split locations and check for partial matches (e.g., same city but different district)
        val parts1 = location1.split(Regex("[,\\s]+")).filter { it.length > 1 }
        val parts2 = location2.split(Regex("[,\\s]+")).filter { it.length > 1 }
        
        val commonParts = parts1.intersect(parts2.toSet())
        if (commonParts.isNotEmpty()) {
            // Score based on how many parts match
            return 50 + (commonParts.size * 10)
        }
        
        // Check for similar words using basic string similarity
        var maxSimilarity = 0
        for (part1 in parts1) {
            for (part2 in parts2) {
                val similarity = calculateStringSimilarity(part1, part2)
                maxSimilarity = maxOf(maxSimilarity, similarity)
            }
        }
        
        return maxSimilarity
    }
    
    // Simple string similarity algorithm (returns a score between 0-100)
    private fun calculateStringSimilarity(str1: String, str2: String): Int {
        val s1 = str1.lowercase()
        val s2 = str2.lowercase()
        
        // If strings are very short, require exact match
        if (s1.length < 3 || s2.length < 3) {
            return if (s1 == s2) 100 else 0
        }
        
        // Check for prefix/suffix match
        if (s1.startsWith(s2) || s1.endsWith(s2) || 
            s2.startsWith(s1) || s2.endsWith(s1)) {
            val matchLength = minOf(s1.length, s2.length)
            val maxLength = maxOf(s1.length, s2.length)
            return (matchLength * 100) / maxLength
        }
        
        // Count common characters
        val commonChars = s1.toSet().intersect(s2.toSet())
        if (commonChars.size > minOf(s1.length, s2.length) / 2) {
            return (commonChars.size * 60) / maxOf(s1.length, s2.length)
        }
        
        return 0
    }
    
    // Combine all suggestion types and return the best ones
    suspend fun getCombinedSuggestions(
        currentUserId: String,
        limit: Int = 10
    ): List<SuggestionWithScore> {
        val mutualSuggestions = getSuggestionsBasedOnMutualFollowers(currentUserId)
        val interactionSuggestions = getSuggestionsBasedOnPostInteractions(currentUserId)
        val locationSuggestions = getSuggestionsBasedOnLocation(currentUserId)
        
        // Debug reasons in suggestions
        mutualSuggestions.forEach { 
            Log.d("FriendRepo", "Mutual suggestion for ${it.user.username}: reason=${it.reason}")
        }
        
        interactionSuggestions.forEach { 
            Log.d("FriendRepo", "Interaction suggestion for ${it.user.username}: reason=${it.reason}")
        }
        
        locationSuggestions.forEach { 
            Log.d("FriendRepo", "Location suggestion for ${it.user.username}: reason=${it.reason}")
        }
        
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
        
        // Add or update with location suggestions
        locationSuggestions.forEach { suggestion ->
            val existingSuggestion = combinedMap[suggestion.user.userId]
            
            if (existingSuggestion != null) {
                // If this user is already suggested, combine the scores and reasons
                // Tránh kết hợp hai lý do liên quan đến vị trí
                val existingHasLocation = existingSuggestion.reason.contains("from", ignoreCase = true) ||
                                         existingSuggestion.reason.contains("Lives in", ignoreCase = true) ||
                                         existingSuggestion.reason.contains("nearby location", ignoreCase = true)
                
                val newReason = if (existingSuggestion.reason != "Suggested for you" && suggestion.reason != "Suggested for you") {
                    if (existingHasLocation) {
                        // Nếu lý do hiện tại đã có thông tin vị trí, không thêm lý do vị trí mới
                        existingSuggestion.reason
                    } else {
                        "${existingSuggestion.reason} and ${suggestion.reason.lowercase()}"
                    }
                } else if (existingSuggestion.reason != "Suggested for you") {
                    existingSuggestion.reason
                } else {
                    suggestion.reason
                }
                
                combinedMap[suggestion.user.userId] = existingSuggestion.copy(
                    score = existingSuggestion.score + suggestion.score,
                    reason = newReason
                )
            } else {
                combinedMap[suggestion.user.userId] = suggestion
            }
        }
        
        // Format final reasons to improve display
        val formattedSuggestions = combinedMap.values.map { suggestion ->
            // Clean up reason to avoid repetitive "and from" in combined reasons
            val cleanedReason = suggestion.reason
                .replace(" and from", ", from") 
                .replace(" and has ", " has ")
            
            suggestion.copy(reason = cleanedReason)
        }
        
        // Chỉ lấy những đề xuất có lý do cụ thể, không lấy "Suggested for you"
        val finalSuggestions = formattedSuggestions
            .filter { it.reason != "Suggested for you" }
            .sortedByDescending { it.score }
            
        // Debug final suggestions
        finalSuggestions.forEach { 
            Log.d("FriendRepo", "Final suggestion for ${it.user.username}: score=${it.score}, reason=${it.reason}")
        }
        
        return finalSuggestions
    }
}

// Data class for suggestions with scores and reasons
data class SuggestionWithScore(
    val user: User,
    val score: Int,
    val mutualConnections: Int,
    val reason: String
) 