package com.forrestgump.ig.ui.screens.messages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import com.forrestgump.ig.ui.screens.messages.components.MessagesTopBar
import com.forrestgump.ig.ui.screens.messages.components.MessagesList


@Composable
fun MessagesScreen(myUsername: String) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MainBackground,
        topBar = {
            MessagesTopBar(myUsername)
        }

    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            MessagesList()
        }
    }
}

