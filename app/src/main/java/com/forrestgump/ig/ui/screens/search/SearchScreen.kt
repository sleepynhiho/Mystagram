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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.forrestgump.ig.R
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.ui.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    uiState: UiState,
    navController: NavController,
    viewModel: SearchViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("Users") }
    var showFilters by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    // Handle empty query clearing suggestions
    LaunchedEffect(searchQuery) {
        if (searchQuery.isEmpty()) {
            // Clear suggestions when query is empty
            viewModel.searchSuggestions("")
        }
    }

    // Consolidated filter states
    var userNameFilter by remember { mutableStateOf(true) }
    var userLocationFilter by remember { mutableStateOf(false) }
    var postContentFilter by remember { mutableStateOf(true) }
    var postTimeFilter by remember { mutableStateOf(false) }

    var locationInput by remember { mutableStateOf("") }
    var timeInput by remember { mutableStateOf("") }
    var fromTimeInput by remember { mutableStateOf("") }
    var toTimeInput by remember { mutableStateOf("") }

    // Function to apply search with current filters
    val applySearch = {
        if (searchQuery.isNotEmpty()) {
            viewModel.searchSuggestions(searchQuery)
        }
    }

    // Effect to reapply filters when filter values change
    LaunchedEffect(
        userNameFilter, userLocationFilter, locationInput,
        postContentFilter, postTimeFilter, fromTimeInput, toTimeInput
    ) {
        if (searchQuery.isNotEmpty()) {
            applySearch()
        }
    }

    val tabs = listOf("Users", "Posts")

    // Select appropriate data source based on whether we're showing suggestions
    val showingSuggestions = searchQuery.isNotEmpty()

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
                            ) { navController.popBackStack() })

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
                        },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = { 
                                if (searchQuery.isNotEmpty()) {
                                    viewModel.searchSuggestions(searchQuery)
                                }
                                focusManager.clearFocus() 
                            }
                        ),
                    )
                }

                // Display friend suggestions only when search query is empty
                if (searchQuery.isEmpty() && uiState.showFriendSuggestions && uiState.friendSuggestions.isNotEmpty()) {
                    FriendSuggestions(
                        suggestions = uiState.friendSuggestions,
                        onUserClick = { userId ->
                            navController.navigate("${Routes.UserProfileScreen.route.replace("{userId}", userId)}")
                        },
                        onFollowClick = { userId ->
                            viewModel.followUser(userId)
                        }
                    )
                }

                // Filter options
                AnimatedVisibility(
                    visible = false,
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
                            .shadow(elevation = 2.dp, shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.search_filters),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            // Reset filters button
                            Text(
                                text = "Reset",
                                color = Color(0xFF3897F0),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clickable {
                                        if (selectedTab == "Users") {
                                            userNameFilter = true
                                            userLocationFilter = false
                                            locationInput = ""
                                        } else {
                                            postContentFilter = true
                                            postTimeFilter = false
                                            fromTimeInput = ""
                                            toTimeInput = ""
                                        }
                                        // Apply search with reset filters
                                        applySearch()
                                    }
                                    .padding(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (selectedTab == "Users") {
                            // Simplified user filter options as toggle buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterToggleButton(
                                    icon = Icons.Outlined.Person,
                                    text = stringResource(id = R.string.search_by_name),
                                    isSelected = userNameFilter,
                                    onToggle = { 
                                        userNameFilter = it
                                        // If turning off name filter but location is also off, enable location
                                        if (!it && !userLocationFilter) {
                                            userLocationFilter = true
                                        }
                                    }
                                )
                                
                                FilterToggleButton(
                                    icon = Icons.Outlined.LocationOn,
                                    text = stringResource(id = R.string.search_by_location),
                                    isSelected = userLocationFilter,
                                    onToggle = { 
                                        userLocationFilter = it
                                        // If turning off location filter but name is also off, enable name
                                        if (!it && !userNameFilter) {
                                            userNameFilter = true
                                        }
                                    }
                                )
                            }

                            if (userLocationFilter) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = locationInput,
                                    onValueChange = { locationInput = it },
                                    label = { Text(text = stringResource(id = R.string.enter_location)) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.background,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                        focusedIndicatorColor = Color(0xFF3897F0),
                                        cursorColor = Color(0xFF3897F0)
                                    ),
                                    trailingIcon = {
                                        if (locationInput.isNotEmpty()) {
                                            IconButton(onClick = { locationInput = "" }) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "Clear",
                                                    tint = Color.Gray
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        } else {
                            // Simplified post filter options as toggle buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterToggleButton(
                                    icon = Icons.Outlined.Description,
                                    text = stringResource(id = R.string.search_by_content),
                                    isSelected = postContentFilter,
                                    onToggle = { 
                                        postContentFilter = it
                                        // If turning off content filter but time is also off, enable time
                                        if (!it && !postTimeFilter) {
                                            postTimeFilter = true
                                        }
                                    }
                                )
                                
                                FilterToggleButton(
                                    icon = Icons.Outlined.CalendarToday,
                                    text = stringResource(id = R.string.search_by_time),
                                    isSelected = postTimeFilter,
                                    onToggle = { 
                                        postTimeFilter = it
                                        // If turning off time filter but content is also off, enable content
                                        if (!it && !postContentFilter) {
                                            postContentFilter = true
                                        }
                                    }
                                )
                            }

                            if (postTimeFilter) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Filter by Month and Year",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Month selection
                                    val months = listOf("January", "February", "March", "April", "May", "June", 
                                                      "July", "August", "September", "October", "November", "December")
                                    var monthDropdownExpanded by remember { mutableStateOf(false) }
                                    var selectedMonth by remember { mutableStateOf("") }
                                    
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .border(
                                                width = 1.dp,
                                                color = Color.Gray.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { monthDropdownExpanded = true }
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = selectedMonth.ifEmpty { "Select Month" },
                                                color = if (selectedMonth.isEmpty()) Color.Gray else MaterialTheme.colorScheme.onBackground,
                                                fontSize = 14.sp
                                            )
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Dropdown",
                                                tint = Color.Gray
                                            )
                                        }
                                        
                                        DropdownMenu(
                                            expanded = monthDropdownExpanded,
                                            onDismissRequest = { monthDropdownExpanded = false },
                                            modifier = Modifier.background(MaterialTheme.colorScheme.background)
                                        ) {
                                            months.forEachIndexed { index, month ->
                                                DropdownMenuItem(
                                                    text = { Text(text = month) },
                                                    onClick = {
                                                        selectedMonth = month
                                                        // Update fromTimeInput with the proper format for filtering
                                                        val monthNum = String.format("%02d", index + 1)
                                                        fromTimeInput = monthNum
                                                        monthDropdownExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    
                                    // Year selection
                                    var yearDropdownExpanded by remember { mutableStateOf(false) }
                                    var selectedYear by remember { mutableStateOf("") }
                                    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                                    val years = (currentYear downTo currentYear - 5).map { it.toString() }
                                    
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .border(
                                                width = 1.dp,
                                                color = Color.Gray.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { yearDropdownExpanded = true }
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = selectedYear.ifEmpty { "Select Year" },
                                                color = if (selectedYear.isEmpty()) Color.Gray else MaterialTheme.colorScheme.onBackground,
                                                fontSize = 14.sp
                                            )
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Dropdown",
                                                tint = Color.Gray
                                            )
                                        }
                                        
                                        DropdownMenu(
                                            expanded = yearDropdownExpanded,
                                            onDismissRequest = { yearDropdownExpanded = false },
                                            modifier = Modifier.background(MaterialTheme.colorScheme.background)
                                        ) {
                                            years.forEach { year ->
                                                DropdownMenuItem(
                                                    text = { Text(text = year) },
                                                    onClick = {
                                                        selectedYear = year
                                                        // Update toTimeInput with the proper format for filtering
                                                        toTimeInput = year
                                                        yearDropdownExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Apply button
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { 
                                if (searchQuery.isNotEmpty()) {
                                    viewModel.searchSuggestions(searchQuery)
                                }
                                showFilters = false  // Close filter panel after applying
                                focusManager.clearFocus()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3897F0)
                            ),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Text(
                                text = "Apply Filters",
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Display initial suggestions when search box is empty 
                if (searchQuery.isEmpty()) {
                    InitialSuggestionsContent(
                        userSuggestions = uiState.userSuggestions,
                        postSuggestions = uiState.postSuggestions,
                        selectedTab = selectedTab,
                        navController = navController,
                        onTabSelected = { selectedTab = it },
                        viewModel = viewModel
                    )
                }
                // If we have search query but no results
                else if (searchQuery.isNotEmpty() && 
                         uiState.userSuggestions.isEmpty() && 
                         uiState.postSuggestions.isEmpty() && 
                         !uiState.isLoading) {
                    EmptySearchResults()
                } 
                // Show query-based suggestions if we have a query
                else if (showingSuggestions) {
                    SuggestionsContent(
                        userSuggestions = uiState.userSuggestions,
                        postSuggestions = uiState.postSuggestions,
                        selectedTab = selectedTab,
                        navController = navController,
                        onTabSelected = { selectedTab = it },
                        viewModel = viewModel
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
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = tab,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                        fontSize = 16.sp, // Unified font size
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Unified indicator size and alignment
                    Box(
                        modifier = Modifier
                            .width(48.dp) // Consistent width
                            .height(3.dp) // Slightly thicker for better visibility
                            .graphicsLayer {
                                alpha = if (isSelected) 1f else 0f
                            }
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                            )
                    )
                }
            }
        }

        Divider(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
            thickness = 0.5.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun FilterToggleButton(
    icon: ImageVector,
    text: String,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onToggle(!isSelected) }
            .background(
                if (isSelected) Color(0xFF3897F0).copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surface
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFF3897F0) else Color.Gray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color(0xFF3897F0) else Color.Gray,
                modifier = Modifier.size(18.dp)
            )
            
            Text(
                text = text,
                color = if (isSelected) Color(0xFF3897F0) else MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

@Composable
fun UsersContent(
    users: List<User>, resultsCount: Int,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 0.dp, top = 8.dp, end = 0.dp, bottom = 80.dp)
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
                UserItem(user = user, navController = navController)
            }
        }
    }
}

@Composable
fun PostsContent(
    posts: List<Post>, resultsCount: Int
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 80.dp),
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
                    contentPadding = PaddingValues(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 80.dp),
                    modifier = Modifier.height((posts.size * 120).dp + 80.dp)
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
            .padding(horizontal = 16.dp, vertical = 8.dp) // Consistent padding
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "$count results",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
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
fun UserItem(user: User, navController: NavController) {
    val currentUserID = FirebaseAuth.getInstance().currentUser?.uid

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (user.userId == currentUserID) {
                    // Navigate to MyProfileScreen for the current user
                    navController.navigate(Routes.MyProfileScreen.route)
                } else {
                    // Navigate to UserProfileScreen for other users
                    navController.navigate("UserProfileScreen/${user.userId}")
                }
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile picture
        Image(
            painter = if (user.profileImage.startsWith("http://") || user.profileImage.startsWith("https://")) {
                rememberAsyncImagePainter(model = user.profileImage)
            } else {
                painterResource(id = R.drawable.default_profile_image)
            },
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
                text = post.timestamp?.let {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                } ?: "Unknown date",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun InitialSuggestionsContent(
    userSuggestions: List<UserSuggestion>,
    postSuggestions: List<PostSuggestion>,
    selectedTab: String,
    navController: NavController,
    onTabSelected: (String) -> Unit,
    viewModel: SearchViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tabs
        TabRow(
            selectedTab = selectedTab, 
            tabs = listOf("Users", "Posts"), 
            onTabSelected = onTabSelected
        )
        
        // Show recommendation header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Recommended for you",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                val count = if (selectedTab == "Users") userSuggestions.size else postSuggestions.size
                Text(
                    text = "$count recommendations",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
            }
        }
        
        if (selectedTab == "Users" && userSuggestions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 0.dp, top = 8.dp, end = 0.dp, bottom = 80.dp)
            ) {
                items(userSuggestions) { suggestion ->
                    UserSuggestionItem(
                        suggestion = suggestion,
                        navController = navController
                    )
                    Divider(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )
                }
            }
        } else if (selectedTab == "Posts" && postSuggestions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 0.dp, top = 8.dp, end = 0.dp, bottom = 80.dp)
            ) {
                items(postSuggestions) { suggestion ->
                    PostSuggestionItem(
                        suggestion = suggestion,
                        navController = navController,
                        viewModel = viewModel
                    )
                    Divider(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )
                }
            }
        } else {
            // No suggestions for the selected tab
            EmptyTabResults(selectedTab)
        }
    }
}

@Composable
fun SuggestionsContent(
    userSuggestions: List<UserSuggestion>,
    postSuggestions: List<PostSuggestion>,
    selectedTab: String,
    navController: NavController,
    onTabSelected: (String) -> Unit,
    viewModel: SearchViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tabs
        TabRow(
            selectedTab = selectedTab, 
            tabs = listOf("Users", "Posts"), 
            onTabSelected = onTabSelected
        )
        
        // Show suggestion header with count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Suggestions",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                val count = if (selectedTab == "Users") userSuggestions.size else postSuggestions.size
                Text(
                    text = "$count results",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
            }
        }
        
        if (selectedTab == "Users" && userSuggestions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 0.dp, top = 8.dp, end = 0.dp, bottom = 80.dp)
            ) {
                items(userSuggestions) { suggestion ->
                    UserSuggestionItem(
                        suggestion = suggestion,
                        navController = navController
                    )
                    Divider(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )
                }
            }
        } else if (selectedTab == "Posts" && postSuggestions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 0.dp, top = 8.dp, end = 0.dp, bottom = 80.dp)
            ) {
                items(postSuggestions) { suggestion ->
                    PostSuggestionItem(
                        suggestion = suggestion,
                        navController = navController,
                        viewModel = viewModel
                    )
                    Divider(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )
                }
            }
        } else {
            // No suggestions for the selected tab
            EmptyTabResults(selectedTab)
        }
    }
}

@Composable
fun UserSuggestionItem(
    suggestion: UserSuggestion,
    navController: NavController
) {
    // Get user location from Firestore
    var userLocation by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(suggestion.userId) {
        // Fetch the complete user data to get the location
        FirebaseFirestore.getInstance().collection("users")
            .document(suggestion.userId)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                userLocation = user?.location ?: ""
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("UserProfileScreen/${suggestion.userId}")
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile picture
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        ) {
            if (suggestion.profilePicture.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(model = suggestion.profilePicture),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Default Profile",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                    tint = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // User info with location
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = suggestion.username,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = suggestion.fullName,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
            
            // Display location if available
            if (!isLoading && userLocation.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Location",
                        tint = Color(0xFF3897F0),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = userLocation,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PostSuggestionItem(
    suggestion: PostSuggestion,
    navController: NavController,
    viewModel: SearchViewModel
) {
    // Get post date from Firestore
    var postTime by remember { mutableStateOf<Date?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(suggestion.postId) {
        // Fetch the complete post data to get the timestamp
        FirebaseFirestore.getInstance().collection("posts")
            .document(suggestion.postId)
            .get()
            .addOnSuccessListener { document ->
                val post = document.toObject(Post::class.java)
                postTime = post?.timestamp
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("PostDetailScreen/${suggestion.postId}")
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Post thumbnail
        Box(
            modifier = Modifier
                .size(48.dp)
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
        
        // Post info with timestamp
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
            
            // Display post time if available
            if (!isLoading && postTime != null) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = "Date",
                        tint = Color(0xFF3897F0),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(postTime!!),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EmptySearchResults() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.SearchOff,
            contentDescription = "No Results",
            tint = Color.Gray,
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No suggestions found",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        
        Text(
            text = "Try a different search term",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun EmptyTabResults(tabName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.SearchOff,
            contentDescription = "No Results",
            tint = Color.Gray,
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No $tabName found",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        
        Text(
            text = "Try a different search term",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun FriendSuggestions(
    suggestions: List<FriendSuggestion>,
    onUserClick: (String) -> Unit,
    onFollowClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "People You Might Know",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(300.dp)
        ) {
            items(suggestions) { suggestion ->
                FriendSuggestionItem(
                    suggestion = suggestion,
                    onUserClick = { onUserClick(suggestion.userId) },
                    onFollowClick = { onFollowClick(suggestion.userId) }
                )
            }
        }
    }
}

@Composable
fun FriendSuggestionItem(
    suggestion: FriendSuggestion,
    onUserClick: () -> Unit,
    onFollowClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onUserClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile image
        Image(
            painter = rememberAsyncImagePainter(
                model = suggestion.profilePicture,
                error = painterResource(id = R.drawable.ic_launcher_foreground)
            ),
            contentDescription = "Profile image",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // User info (name, username, reason)
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight()
        ) {
            Text(
                text = suggestion.username,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = suggestion.fullName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Text(
                text = suggestion.reason,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Follow button
        Button(
            onClick = onFollowClick,
            modifier = Modifier
                .wrapContentHeight()
                .height(36.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
        ) {
            Text(
                text = "Follow",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}