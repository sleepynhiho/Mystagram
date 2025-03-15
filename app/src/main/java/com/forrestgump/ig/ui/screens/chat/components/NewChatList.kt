package com.forrestgump.ig.ui.screens.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.forrestgump.ig.R
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.utils.constants.Utils.MainBackground

@Composable
fun NewChatList(
    users: List<User>,
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    myUsername: String
) {
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MainBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .height(40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = stringResource(R.string.search_new_chat),
                fontSize = 14.sp,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5F),
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(5.dp)
            )

            // Sticky Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .padding(5.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (searchText.isEmpty()) {
                    Text(
                        text = stringResource(R.string.search_placeholder),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                BasicTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 14.sp
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {})
                )
            }
        }

        val filteredUsers = users.filter {
            it.username.contains(
                searchText,
                ignoreCase = true
            ) && it.username != myUsername
        }

        // User List (Scrollable)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MainBackground)
                .padding(horizontal = 10.dp)
        ) {
            item { NewChatHeader() }
            items(filteredUsers) { user ->
                NewChatListItem(
                    user = user, navHostController = navHostController
                )
            }
        }
    }
}


@Composable
fun NewChatHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MainBackground)
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 10.dp),
            text = stringResource(R.string.new_chat_header),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp
            )
        )
    }
}

@Composable
fun NewChatListItem(
    user: User,
    navHostController: NavHostController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MainBackground)
            .height(72.dp)
            .clickable {
                navHostController.navigate("ChatBoxScreen/fix")
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        Surface(
            modifier = Modifier
                .size(56.dp),
            shape = CircleShape,
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = user.profileImage,
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(id = R.string.profile_image)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = user.username,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun NewChatListPreview() {
    // Sample users with real profile images
    val sampleUsers = listOf(
        User(
            userId = "1",
            username = "Alice",
            profileImage = "https://randomuser.me/api/portraits/women/1.jpg"
        ),
        User(
            userId = "2",
            username = "Bob",
            profileImage = "https://randomuser.me/api/portraits/men/2.jpg"
        ),
        User(
            userId = "3",
            username = "Charlie",
            profileImage = "https://randomuser.me/api/portraits/men/3.jpg"
        ),
        User(
            userId = "4",
            username = "David",
            profileImage = "https://randomuser.me/api/portraits/men/4.jpg"
        ),
        User(
            userId = "5",
            username = "Emma",
            profileImage = "https://randomuser.me/api/portraits/women/5.jpg"
        ),
    )

    // Fake NavController for preview
    val fakeNavController = rememberNavController()

    NewChatList(
        users = sampleUsers,
        innerPadding = PaddingValues(8.dp),
        navHostController = fakeNavController,
        myUsername = "Alice"
    )
}
