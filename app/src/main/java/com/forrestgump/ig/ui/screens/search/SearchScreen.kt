package com.forrestgump.ig.ui.screens.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.forrestgump.ig.ui.screens.profile.UiState
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import com.forrestgump.ig.utils.constants.Utils.onSurface
import com.google.android.material.motion.MaterialMainContainerBackHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(uiState: UiState) {
    var searchQuery by remember { mutableStateOf("wuthering waves") }
    var selectedTab by remember { mutableStateOf("Accounts") }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val tabs = listOf("Posts", "Accounts")

    val accounts = listOf(
        Account(
            username = "wuthering_waves",
            description = "Wuthering Waves",
            imageUrl = "https://picsum.photos/seed/101/300"
        ),
        Account(
            username = "wutheringwave.id",
            description = "WW-id | Wuthering Waves Indonesia",
            imageUrl = "https://picsum.photos/seed/102/300",
            hasColorfulBorder = true
        ),
        Account(
            username = "wutheringwaves",
            description = "Wuthering Waves",
            imageUrl = "https://picsum.photos/seed/103/300"
        ),
        Account(
            username = "wutheringwaves.build",
            description = "Wuthering Waves Build",
            imageUrl = "https://picsum.photos/seed/104/300",
            hasColorfulBorder = true
        ),
        Account(
            username = "wutheringwavez.id",
            description = "Wuthering Waves",
            imageUrl = "https://picsum.photos/seed/105/300",
            hasColorfulBorder = true
        )
    )

    val posts = List(6) { index ->
        "https://picsum.photos/seed/${index + 200}/300"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
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
                    tint = onSurface,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { }
                )

                Spacer(modifier = Modifier.width(12.dp))

                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MainBackground)
                        .padding(2.dp), // Thêm padding
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = onSurface,
                        disabledContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = onSurface
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = onSurface
                                )
                            }
                        }
                    },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                )
            }
            Divider(color = Color(0xFF333333), thickness = 1.dp)
            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(MainBackground),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                tabs.forEach { tab ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f) // Chia đều không gian cho cả hai tab
                            .clickable {
                                selectedTab = tab
                            } // Cho phép bấm bất kỳ chỗ nào trong tab
                            .padding(vertical = 8.dp) // Tăng vùng bấm
                    ) {
                        Text(
                            text = tab,
                            color = if (tab == selectedTab) onSurface else Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = if (tab == selectedTab) FontWeight.Bold else FontWeight.Normal
                        )

                        if (tab == selectedTab) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(2.dp)
                                    .background(MainBackground)
                            )
                        }
                    }
                }
            }


            Divider(color = Color(0xFF333333), thickness = 1.dp)

            // Content based on selected tab
            when (selectedTab) {
                "Accounts" -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MainBackground)
                    ) {
                        item {
                            Text(
                                text = "Accounts",
                                color = onSurface,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        items(accounts) { account ->
                            AccountItem(account = account)
                        }
                    }
                }

                "Posts" -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Text(
                                text = "Posts",
                                color = onSurface,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        item {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(800.dp)
                            ) {
                                items(posts) { imageUrl ->
                                    PostItem(imageUrl = imageUrl)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class Account(
    val username: String,
    val description: String,
    val imageUrl: String,
    val hasColorfulBorder: Boolean = false
)

@Composable
fun AccountItem(account: Account) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            if (account.hasColorfulBorder) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFF0000),
                                    Color(0xFFFF9800),
                                    Color(0xFFFFEB3B)
                                )
                            ),
                            shape = CircleShape
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
                color = onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = account.description,
                color = onSurface,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PostItem(imageUrl: String) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(0.5.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = imageUrl),
            contentDescription = "Post",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // For the post with view count
        if (imageUrl.contains("202")) {
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
                    text = "301K",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}