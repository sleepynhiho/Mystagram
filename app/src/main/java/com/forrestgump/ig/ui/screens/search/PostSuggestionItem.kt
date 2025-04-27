package com.forrestgump.ig.ui.screens.search

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

// Simple cache to avoid reloading user data multiple times
object UserCache {
    private val userCache = mutableMapOf<String, User>()
    
    fun getUser(userId: String): User? {
        return userCache[userId]
    }
    
    fun cacheUser(user: User) {
        userCache[user.userId] = user
    }
    
    fun clearCache() {
        userCache.clear()
    }
}

// Post cache to store post timestamps
object PostCache {
    private val postTimestamps = mutableMapOf<String, Date>()
    
    fun getPostTime(postId: String): Date? {
        return postTimestamps[postId]
    }
    
    fun cachePostTime(postId: String, timestamp: Date) {
        postTimestamps[postId] = timestamp
    }
    
    fun clearCache() {
        postTimestamps.clear()
    }
    
    // Check if cache is older than 60 seconds
    fun isCacheValid(postId: String): Boolean {
        val timestamp = postTimestamps[postId] ?: return false
        val currentTime = Date()
        // Cache valid for 60 seconds to ensure fresh data on app restart
        return (currentTime.time - timestamp.time) < 60000
    }
}

@Composable
fun EnhancedPostSuggestionItem(
    suggestion: PostSuggestion,
    navController: NavController,
    viewModel: SearchViewModel,
    suggestedUsers: List<FriendSuggestion> = emptyList()
) {
    // Check for cached date first to avoid flickering
    val cachedTime = remember { PostCache.getPostTime(suggestion.postId) }
    
    // Get post date and user info from Firestore
    var postTime by remember { mutableStateOf<Date?>(cachedTime) }
    var postOwner by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(cachedTime == null) } // Only show loading if no cached data
    
    // Find if this post's user is in suggested users for username display
    val matchingSuggestedUser = suggestedUsers.find { it.userId == suggestion.userId }
    
    // Use username from suggested users list if available (no need to wait for Firestore)
    val displayUsername = remember(matchingSuggestedUser, postOwner) {
        when {
            postOwner != null -> postOwner?.username ?: "Loading..."
            matchingSuggestedUser != null -> matchingSuggestedUser.username
            else -> "Loading..."
        }
    }
    
    // Always refresh post timestamp data - this is key to fixing the issue
    LaunchedEffect(suggestion.postId) {
        // Skip Firestore query if we already have a valid cached timestamp
        if (cachedTime == null || !PostCache.isCacheValid(suggestion.postId)) {
            // Always fetch the post data to get the latest timestamp
            FirebaseFirestore.getInstance().collection("posts")
                .document(suggestion.postId)
                .get()
                .addOnSuccessListener { document ->
                    val post = document.toObject(Post::class.java)
                    post?.timestamp?.let { timestamp ->
                        postTime = timestamp
                        // Cache the timestamp
                        PostCache.cachePostTime(suggestion.postId, timestamp)
                    }
                    
                    // If we already have the user data, no need to fetch it again
                    if (postOwner == null && post != null) {
                        // Check if user is already in cache
                        val cachedUser = UserCache.getUser(post.userId)
                        if (cachedUser != null) {
                            postOwner = cachedUser
                            isLoading = false
                        } else {
                            // Fetch user data if not in cache
                            FirebaseFirestore.getInstance().collection("users")
                                .document(post.userId)
                                .get()
                                .addOnSuccessListener { userDoc ->
                                    val user = userDoc.toObject(User::class.java)
                                    postOwner = user
                                    
                                    // Cache the user data for future use
                                    if (user != null) {
                                        UserCache.cacheUser(user)
                                    }
                                    
                                    isLoading = false
                                }
                                .addOnFailureListener {
                                    isLoading = false
                                }
                        }
                    } else {
                        isLoading = false
                    }
                }
                .addOnFailureListener {
                    isLoading = false
                }
        } else {
            // If we have cached data, mark as not loading immediately
            isLoading = false
        }
    }

    // Generate reason text based on available data
    val reasonToShow = if (matchingSuggestedUser != null) {
        "From ${matchingSuggestedUser.username} (${matchingSuggestedUser.reason})"
    } else {
        "Recommended post"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("PostDetailScreen/${suggestion.postId}")
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        // User info row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User profile picture
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            ) {
                if (postOwner?.profileImage?.isNotEmpty() == true) {
                    Image(
                        painter = rememberAsyncImagePainter(model = postOwner?.profileImage),
                        contentDescription = "User Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Default Profile",
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.Center),
                        tint = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Username - use displayUsername determined above
            Text(
                text = displayUsername,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Post date with better handling
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = "Date",
                    tint = Color(0xFF3897F0),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                if (isLoading) {
                    // Show a mini loading indicator instead of text
                    CircularProgressIndicator(
                        modifier = Modifier.size(10.dp),
                        strokeWidth = 1.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = postTime?.let { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it) }
                            ?: "Date unavailable",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Post content row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Post thumbnail
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (suggestion.imageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = suggestion.imageUrl),
                        contentDescription = "Post Thumbnail",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.PlayCircle,
                        contentDescription = "Post",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Post caption
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = suggestion.caption,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Reason for recommendation
                Text(
                    text = reasonToShow,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
} 