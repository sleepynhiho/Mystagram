package com.forrestgump.ig.ui.screens.add.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.forrestgump.ig.R

@Composable
fun AddStoryTopBar(
    onBackClicked: () -> Unit,
    onAddTextClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(color = Color.Transparent)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(
            onClick = { onBackClicked() },
            modifier = Modifier
                .size(40.dp)

        ) {
            Icon(
                painter = painterResource(id = R.drawable.back_story),
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .background(color = Color(0xFF404446).copy(alpha = 0.3f), shape = CircleShape)
                    .padding(5.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = { onAddTextClicked() },
            modifier = Modifier
                .size(40.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.text),
                tint = Color.White,
                contentDescription = "Add Text to story",
                modifier = Modifier
                    .background(color = Color(0xFF404446).copy(alpha = 0.3f), shape = CircleShape)
                    .padding(5.dp)
            )
        }
    }
}

@Preview
@Composable
fun AddStoryTopBarPreview() {
    AddStoryTopBar(
        onBackClicked = {},
        onAddTextClicked = {}
    )
}