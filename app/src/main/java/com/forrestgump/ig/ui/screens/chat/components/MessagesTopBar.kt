package com.forrestgump.ig.ui.screens.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.forrestgump.ig.R
import com.forrestgump.ig.utils.constants.Utils.MainBackground

@Composable
fun MessagesTopBar(
    myUsername: String,
    navHostController: NavHostController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .background(color = MainBackground),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.width(15.dp))

        Row(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.5f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                modifier = Modifier
                    .size(37.dp)
                    .padding(5.dp)
                    .clickable { navHostController.popBackStack() },
                painter = painterResource(id = R.drawable.back),
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = stringResource(id = R.string.back_home)
            )

            Spacer(modifier = Modifier.width(20.dp))

            Text(
                text = myUsername,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        Icon(
            modifier = Modifier
                .size(37.dp)
                .padding(5.dp),
            painter = painterResource(id = R.drawable.new_message),
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = stringResource(id = R.string.add_new_message)
        )
        Spacer(modifier = Modifier.width(15.dp))

    }
}


@Preview
@Composable
fun MessagesTopBarPreview() {
    MessagesTopBar(
        myUsername = "sleepy",
        navHostController = rememberNavController()
    )
}
