package com.forrestgump.ig.ui.screens.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forrestgump.ig.R

@Composable
fun ChatBoxInputBar(
    onSendMessage: (String) -> Unit,
    onUploadImage: () -> Unit
) {
    var message by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, bottom = 10.dp, end = 10.dp)
            .height(50.dp)
            .background(
                color = Color(0xFFefefef),
                shape = RoundedCornerShape(22.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = message,
                onValueChange = { message = it },
                placeholder = {
                    Text(
                        text = "Message...",
                        fontSize = 11.sp
                    )
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFefefef),
                    focusedContainerColor = Color(0xFFefefef),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color(0xFF65676b),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 5.dp)
                    .heightIn(min = 40.dp),
                shape = RoundedCornerShape(20.dp),
            )

            if (message.isBlank()) {
                IconButton(
                    onClick = onUploadImage,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.upload_image),
                        contentDescription = stringResource(id = R.string.upload_image),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        onSendMessage(message)
                        message = ""
                    },
                    modifier = Modifier
                        .background(color = Color(0xFF3897F0), shape = RoundedCornerShape(20.dp))
                        .padding(vertical = 3.dp, horizontal = 9.dp)
                        .size(32.dp)

                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.send_message),
                        contentDescription = stringResource(id = R.string.send_message),
                        tint = Color.White
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun ChatBoxInputBarPreview() {
    ChatBoxInputBar(
        onSendMessage = {},
        onUploadImage = {}
    )
}