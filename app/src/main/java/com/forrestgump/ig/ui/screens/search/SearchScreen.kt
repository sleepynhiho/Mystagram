package com.forrestgump.ig.ui.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.forrestgump.ig.R
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.utils.constants.Utils.onSurface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    uiState: UiState
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("Users") }
    var showFilters by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    // Filter states
    var userNameFilter by remember { mutableStateOf(true) }
    var userLocationFilter by remember { mutableStateOf(false) }
    var postContentFilter by remember { mutableStateOf(true) }
    var postTimeFilter by remember { mutableStateOf(false) }

    val tabs = listOf("Users", "Posts")

    // Filter users based on search criteria
    val filteredUsers = uiState.users.filter { user ->
        val matchesName = if (userNameFilter) {
            user.username.contains(searchQuery, ignoreCase = true) ||
                    user.fullName.contains(searchQuery, ignoreCase = true)
        } else true

        val matchesLocation = if (userLocationFilter) {
            user.location.contains(searchQuery, ignoreCase = true)
        } else true

        matchesName && matchesLocation
    }

    // Filter posts based on search criteria
    val filteredPosts = uiState.posts.filter { post ->
        val matchesContent = if (postContentFilter) {
            post.caption.contains(searchQuery, ignoreCase = true)
        } else true

        val matchesTime = if (postTimeFilter) {
            post.timestamp?.toString()?.contains("-05-") ?: false
        } else true

        matchesContent && matchesTime
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
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
                                        onClick = { searchQuery = "" },
                                        modifier = Modifier.size(32.dp)
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

                // Filter options
                AnimatedVisibility(
                    visible = showFilters,
                    enter = fadeIn(animationSpec = tween(200)) + expandVertically(
                        animationSpec = tween(250)
                    ),
                    exit = fadeOut(animationSpec = tween(200)) + shrinkVertically(
                        animationSpec = tween(250)
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

                        if (selectedTab == "Users") {
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

                // Tabs
                TabRow(
                    selectedTab = selectedTab, tabs = tabs, onTabSelected = { selectedTab = it })

                // Content based on selected tab
                when (selectedTab) {
                    "Users" -> {
                        UsersContent(
                            users = filteredUsers, resultsCount = filteredUsers.size
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
fun UsersContent(
    users: List<User>, resultsCount: Int
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 35.dp)
    ) {
        item {
            ResultsHeader(
                title = "Users", count = resultsCount
            )
        }

        if (users.isEmpty()) {
            item {
                EmptyResults(message = stringResource(id = R.string.no_acc_found))
            }
        } else {
            items(users) { user ->
                UserItem(user = user)
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
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "$count results",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 12.sp
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
fun UserItem(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile picture
        Image(
            painter = rememberAsyncImagePainter(model = user.profileImage),
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .border(1.dp, Color.Gray, CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = user.username,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = user.fullName,
                color = Color.Gray,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (user.location.isNotEmpty()) {
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
                        text = user.location,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
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
                text = "Follow",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
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
            .clickable { }
    ) {
        // Post image
        Image(
            painter = rememberAsyncImagePainter(model = post.mediaUrls.firstOrNull()),
            contentDescription = post.caption,
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
        post.reactions["views"]?.size?.let { views ->
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
                val date = inputFormat.parse(post.timestamp.toString())
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