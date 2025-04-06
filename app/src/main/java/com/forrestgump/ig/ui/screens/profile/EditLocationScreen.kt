package com.forrestgump.ig.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLocationScreen(
    viewModel: ProfileViewModel,
    navController: NavController,
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentLocation = uiState.curUser.location
    var searchQuery by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf(currentLocation) }

    // Load cities from JSON file
    val context = LocalContext.current
    val vietnamProvinces = remember {
        try {
            val jsonString = context.assets.open("cities.json").bufferedReader().use { it.readText() }
            val jsonObject = org.json.JSONObject(jsonString)
            val citiesArray = jsonObject.getJSONArray("vietnamProvinces")
            List(citiesArray.length()) { i -> citiesArray.getString(i) }
        } catch (e: Exception) {
            // Fallback to hardcoded list if JSON loading fails
            listOf(
                "Not Found assets/cities.json"
            )
        }
    }

    // Filter locations based on search query
    val filteredLocations = vietnamProvinces.filter {
        it.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chỉnh sửa vị trí",
                        fontSize = 16.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Button "Lưu" - save button
            Button(
                onClick = {
                    viewModel.updateLocalUserLocation(selectedLocation)
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(4.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF00BCD4), Color(0xFF2196F3))
                        ),
                        shape = RoundedCornerShape(40.dp)
                    ),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Text(
                    text = "Lưu",
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Tìm kiếm tỉnh/thành phố") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            // Current location display
            if (currentLocation.isNotEmpty()) {
                Text(
                    text = "Vị trí hiện tại: $currentLocation",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // List of locations
            LazyColumn {
                items(filteredLocations) { location ->
                    LocationItem(
                        locationName = location,
                        isSelected = location == selectedLocation,
                        onSelect = { selectedLocation = location }
                    )
                }
            }
        }
    }
}

@Composable
fun LocationItem(
    locationName: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = locationName,
            style = MaterialTheme.typography.bodyLarge
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color(0xFF2196F3)
            )
        }
    }

    Divider(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
        color = Color.LightGray.copy(alpha = 0.5f)
    )
}