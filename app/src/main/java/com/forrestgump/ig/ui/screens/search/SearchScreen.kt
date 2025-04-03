package com.forrestgump.ig.ui.screens.search

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.forrestgump.ig.R
import com.forrestgump.ig.ui.screens.profile.UiState
import java.text.SimpleDateFormat
import java.util.*
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import com.forrestgump.ig.utils.constants.Utils.onSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    uiState: UiState
) {
    var searchQuery by remember { mutableStateOf("wuthering waves") }
    var selectedTab by remember { mutableStateOf("Accounts") }
    var showFilters by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    // Filter states
    var userNameFilter by remember { mutableStateOf(true) }
    var userLocationFilter by remember { mutableStateOf(false) }
    var postContentFilter by remember { mutableStateOf(true) }
    var postTimeFilter by remember { mutableStateOf(false) }

    val tabs = listOf("Posts", "Accounts")

    // Sample data with more details for filtering
    val accounts = listOf(
        Account(
            username = "wuthering_waves",
            displayName = "Wuthering Waves",
            location = "Global",
            imageUrl = "https://picsum.photos/seed/101/300"
        ),
        Account(
            username = "wutheringwave.id",
            displayName = "WW-id | Wuthering Waves Indonesia",
            location = "Indonesia",
            imageUrl = "https://picsum.photos/seed/102/300",
            hasColorfulBorder = true
        ),
        Account(
            username = "wutheringwaves",
            displayName = "Wuthering Waves",
            location = "Japan",
            imageUrl = "https://picsum.photos/seed/103/300"
        ),
        Account(
            username = "wutheringwaves.build",
            displayName = "Wuthering Waves Build",
            location = "United States",
            imageUrl = "https://picsum.photos/seed/104/300",
            hasColorfulBorder = true
        ),
        Account(
            username = "wutheringwavez.id",
            displayName = "Wuthering Waves",
            location = "Indonesia",
            imageUrl = "https://picsum.photos/seed/105/300",
            hasColorfulBorder = true
        ),
        Account(
            username = "wuthering_waves",
            displayName = "Wuthering Waves",
            location = "Global",
            imageUrl = "https://picsum.photos/seed/106/300",
            hasColorfulBorder = true
        ),
        Account(
            username = "wutheringwave.id",
            displayName = "WW-id | Wuthering Waves Indonesia",
            location = "Indonesia",
            imageUrl = "https://picsum.photos/seed/107/300",
            hasColorfulBorder = true
        ),
        Account(
            username = "wutheringwaves",
            displayName = "Wuthering Waves",
            location = "Japan",
            imageUrl = "https://picsum.photos/seed/108/300",
            hasColorfulBorder = true
        ),
        Account(
            username = "wutheringwaves.build",
            displayName = "Wuthering Waves Build",
            location = "United States",
            imageUrl = "https://picsum.photos/seed/109/300",
            hasColorfulBorder = true
        )
    )

    val posts = listOf(
        Post(
            id = "post1",
            content = "Wuthering Waves gameplay preview",
            imageUrl = "https://picsum.photos/seed/201/300",
            timestamp = "2023-05-15T14:30:00",
            username = "wuthering_waves"
        ), Post(
            id = "post2",
            content = "New character reveal for Wuthering Waves",
            imageUrl = "https://picsum.photos/seed/202/300",
            timestamp = "2023-05-20T09:15:00",
            username = "wutheringwave.id",
            views = 301000
        ), Post(
            id = "post3",
            content = "Wuthering Waves beta testing announcement",
            imageUrl = "https://picsum.photos/seed/203/300",
            timestamp = "2023-05-25T18:45:00",
            username = "wutheringwaves"
        ), Post(
            id = "post4",
            content = "Best builds for Wuthering Waves characters",
            imageUrl = "https://picsum.photos/seed/204/300",
            timestamp = "2023-06-01T11:20:00",
            username = "wutheringwaves.build"
        ), Post(
            id = "post5",
            content = "Wuthering Waves Indonesia community meetup",
            imageUrl = "https://picsum.photos/seed/205/300",
            timestamp = "2023-06-05T16:00:00",
            username = "wutheringwavez.id"
        ), Post(
            id = "post6",
            content = "Wuthering Waves official soundtrack release",
            imageUrl = "https://picsum.photos/seed/206/300",
            timestamp = "2023-06-10T13:30:00",
            username = "wuthering_waves"
        ), Post(
            id = "post7",
            content = "New update for Wuthering Waves gameplay",
            imageUrl = "https://picsum.photos/seed/207/300",
            timestamp = "2023-06-15T10:45:00",
            username = "wutheringwaves.build",
            views = 250000
        ), Post(
            id = "post8",
            content = "Wuthering Waves beta launch event",
            imageUrl = "https://picsum.photos/seed/208/300",
            timestamp = "2023-06-20T17:20:00",
            username = "wutheringwavez.id"
        )
    )

    // Filter accounts based on search criteria
    val filteredAccounts = accounts.filter { account ->
        val matchesName = if (userNameFilter) {
            account.username.contains(
                searchQuery,
                ignoreCase = true
            ) || account.displayName.contains(searchQuery, ignoreCase = true)
        } else true

        val matchesLocation = if (userLocationFilter) {
            account.location.contains(searchQuery, ignoreCase = true)
        } else true

        matchesName || matchesLocation
    }

    // Filter posts based on search criteria
    val filteredPosts = posts.filter { post ->
        val matchesContent = if (postContentFilter) {
            post.content.contains(searchQuery, ignoreCase = true)
        } else true

        val matchesTime = if (postTimeFilter) {
            // Simple example: check if the post is from May (05)
            post.timestamp.contains("-05-")
        } else true

        matchesContent || matchesTime
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Search bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { })

                Spacer(modifier = Modifier.width(12.dp))

                // Enhanced search field with better styling
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight()
                        .focusRequester(focusRequester)
                        .background(MaterialTheme.colorScheme.background),
                    colors = TextFieldDefaults.colors(
                        cursorColor = MaterialTheme.colorScheme.onBackground,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        Row {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" }, modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            IconButton(
                                onClick = { showFilters = !showFilters },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.FilterList,
                                    contentDescription = "Filters",
                                    tint = if (showFilters) Color(0xFF3897F0) else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),

                    )
            }

            // Filter options with improved animation and styling
            AnimatedVisibility(
                visible = showFilters,
                enter = fadeIn(animationSpec = tween(200)) + expandVertically(
                    animationSpec = tween(
                        250
                    )
                ),
                exit = fadeOut(animationSpec = tween(200)) + shrinkVertically(
                    animationSpec = tween(
                        250
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.search_filters),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Enhanced filter options with better styling
                    if (selectedTab == "Accounts") {
                        FilterOption(
                            title = stringResource(id = R.string.search_by_name),
                            isChecked = userNameFilter,
                            onCheckedChange = { userNameFilter = it },
                            icon = Icons.Outlined.Person
                        )

                        FilterOption(
                            title = stringResource(id = R.string.search_by_location),
                            isChecked = userLocationFilter,
                            onCheckedChange = { userLocationFilter = it },
                            icon = Icons.Outlined.LocationOn
                        )
                    } else {
                        FilterOption(
                            title = stringResource(id = R.string.search_by_content),
                            isChecked = postContentFilter,
                            onCheckedChange = { postContentFilter = it },
                            icon = Icons.Outlined.Description
                        )

                        FilterOption(
                            title = stringResource(id = R.string.search_by_time),
                            isChecked = postTimeFilter,
                            onCheckedChange = { postTimeFilter = it },
                            icon = Icons.Outlined.CalendarToday
                        )
                    }
                }
            }

            // Enhanced tabs with better styling and animations
            TabRow(
                selectedTab = selectedTab, tabs = tabs, onTabSelected = { selectedTab = it })

            // Content based on selected tab
            when (selectedTab) {
                "Accounts" -> {
                    AccountsContent(
                        accounts = filteredAccounts, resultsCount = filteredAccounts.size
                    )
                }

                "Posts" -> {
                    PostsContent(
                        posts = filteredPosts, resultsCount = filteredPosts.size
                    )
                }
            }
        }
    }
}

@Composable
fun TabRow(
    selectedTab: String, tabs: List<String>, onTabSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEach { tab ->
                val isSelected = tab == selectedTab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(tab) }
                        .padding(vertical = 8.dp)) {
                    Text(
                        text = tab,
                        color = if (isSelected) onSurface else Color.Gray,
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Animated indicator
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(2.dp)
                            .graphicsLayer {
                                alpha = if (isSelected) 1f else 0f
                            }
                            .background(
                                if (isSelected) Color(0xFF3897F0) else Color.Transparent
                            ))
                }
            }
        }

        Divider(
            color = Color(0xFF333333), thickness = 0.5.dp, modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun FilterOption(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onCheckedChange(!isChecked) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF3897F0),
                uncheckedColor = Color.Gray,
                checkmarkColor = Color.White
            )
        )

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isChecked) Color(0xFF3897F0) else Color.Gray,
            modifier = Modifier
                .size(18.dp)
                .padding(end = 4.dp)
        )

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun AccountsContent(
    accounts: List<Account>, resultsCount: Int
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 35.dp)
    ) {
        item {
            ResultsHeader(
                title = "Accounts", count = resultsCount
            )
        }

        if (accounts.isEmpty()) {
            item {
                EmptyResults(message = stringResource(id = R.string.no_acc_found))
            }
        } else {
            items(accounts) { account ->
                AccountItem(account = account)
            }
        }
    }
}

@Composable
fun PostsContent(
    posts: List<Post>, resultsCount: Int
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            ResultsHeader(
                title = "Posts", count = resultsCount
            )
        }

        if (posts.isEmpty()) {
            item {
                EmptyResults(message = stringResource(id = R.string.no_post_found))
            }
        } else {
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height((posts.size * 120).dp)
                ) {
                    items(posts) { post ->
                        PostItem(post = post)
                    }
                }
            }
        }
    }
}

@Composable
fun ResultsHeader(
    title: String, count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "$count results", color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp
            )
        }
    }
}

@Composable
fun EmptyResults(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.SearchOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(48.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = message, color = Color.Gray, fontSize = 16.sp
            )
        }
    }
}

@Composable
fun AccountItem(account: Account) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        // Enhanced profile picture with better styling
        Box {
            if (account.hasColorfulBorder) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color(0xFFFF0000),
                                    Color(0xFFFF9800),
                                    Color(0xFFFFEB3B),
                                    Color(0xFF4CAF50),
                                    Color(0xFF2196F3),
                                    Color(0xFF9C27B0),
                                    Color(0xFFFF0000)
                                )
                            ), shape = CircleShape
                        )
                )
            }

            Image(
                painter = rememberAsyncImagePainter(model = account.imageUrl),
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(if (account.hasColorfulBorder) 52.dp else 56.dp)
                    .clip(CircleShape)
                    .align(if (account.hasColorfulBorder) Alignment.Center else Alignment.TopStart)
                    .border(
                        width = if (account.hasColorfulBorder) 0.dp else 1.dp,
                        color = Color.Gray,
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = account.username,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = account.displayName,
                color = Color.Gray,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = "Location",
                    tint = Color(0xFF3897F0),
                    modifier = Modifier.size(12.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = account.location, color = Color.Gray, fontSize = 12.sp
                )
            }
        }

        // Follow button
        Button(
            onClick = { },
            modifier = Modifier
                .height(32.dp)
                .width(80.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3897F0)
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
        ) {
            Text(
                text = "Follow", fontSize = 12.sp, fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PostItem(post: Post) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(0.5.dp)
            .clickable { }) {
        // Enhanced post image with subtle shadow
        Image(
            painter = rememberAsyncImagePainter(model = post.imageUrl),
            contentDescription = post.content,
            modifier = Modifier
                .fillMaxSize()
                .shadow(1.dp),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay at the bottom for better text visibility
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent, Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        // For posts with view count
        post.views?.let { views ->
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.PlayCircle,
                    contentDescription = "Views",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "${views / 1000}K",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Enhanced date display
        val formattedDate = remember(post.timestamp) {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                val date = inputFormat.parse(post.timestamp)
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                "Unknown date"
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Text(
                text = formattedDate,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

data class Account(
    val username: String,
    val displayName: String,
    val location: String,
    val imageUrl: String,
    val hasColorfulBorder: Boolean = false
)

data class Post(
    val id: String,
    val content: String,
    val imageUrl: String,
    val timestamp: String,
    val username: String,
    val views: Int? = null
)