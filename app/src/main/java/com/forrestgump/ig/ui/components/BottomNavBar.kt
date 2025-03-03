package com.forrestgump.ig.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.forrestgump.ig.R
import com.forrestgump.ig.ui.navigation.Routes
import com.forrestgump.ig.utils.constants.Utils.MainBackground

@Composable
fun BottomNavBar(
    myProfileImage: String,
    navHostController: NavHostController
) {
    val items = Routes.Items.bottomNavItems
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = Color(0xFFDBDBDB),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .height(48.dp),
        color = MainBackground,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                BottomNavBarItem(
                    isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                    item = item,
                    isMyProfileScreen = item.route == Routes.MyProfileScreen.route,
                    myProfileImage = myProfileImage,
                    onClick = {
                        val route = item.route
                        navHostController.navigate(route) {
                            popUpTo(Routes.HomeScreen.route) // always back to home screen
                            launchSingleTop = true // avoid tap multiple times
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavBarItem(
    isSelected: Boolean,
    item: Routes,
    isMyProfileScreen: Boolean = false,
    myProfileImage: String,
    onClick: (Routes) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val image = myProfileImage.ifEmpty { R.drawable.default_profile_img }
    if (!isMyProfileScreen) {
        Box(
            modifier = Modifier.clickable(
                indication = null,
                interactionSource = interactionSource,
                onClick = { onClick(item) }
            ),
            contentAlignment = Alignment.Center,
            content = {
                Icon(
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = interactionSource,
                        onClick = { onClick(item) }
                    ),
                    painter = painterResource(id = if (isSelected) item.iconFilled!! else item.iconOutlined!!),
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = item.name
                )
            }
        )
    } else {
        Surface(
            modifier = Modifier
                .size(24.dp)
                .clickable(
                    indication = null,
                    interactionSource = interactionSource,
                    onClick = { onClick(item) }
                ),
            shape = CircleShape,
            color = Color.LightGray,
            border = BorderStroke(
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    Color.Transparent
                },
                width = 1.5.dp
            )
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = image,
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(id = R.string.profile_image)
            )
        }
    }
}

@Preview
@Composable
fun BottomNavBarPreview() {
    BottomNavBar(
        myProfileImage = "",
        navHostController = rememberNavController()
    )
}