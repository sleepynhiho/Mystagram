package com.forrestgump.ig.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.forrestgump.ig.data.models.Comment
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import com.forrestgump.ig.utils.constants.formatAsElapsedTime
import java.util.Date

@Composable
fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .padding(10.dp)
            .background(MainBackground)
    ) {
        Surface(
            modifier = Modifier.size(30.dp),
            shape = CircleShape
        ) {
            // Author's profile image
            AsyncImage(
                model = comment.profileImage,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                contentDescription = comment.username
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column (
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.username,
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Story's timestamp
                comment.timestamp?.let {
                    androidx.compose.material3.Text(
                        text = it.formatAsElapsedTime(),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Normal
                        ),
                    )
                }
            }

            Text(
                text = comment.text,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun CommentItemPreview() {
    CommentItem(
        comment = Comment(
            commentId = "cmt123",
            postId = "post456",
            userId = "userA",
            username = "alice",
            profileImage = "https://example.com/alice.jpg",
            text = "Bài viết hay quá!",
            timestamp = Date()
        )
    )
}
