package com.example.bugsgame

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bugsgame.ui.theme.Green

@Composable
fun WelcomeScreen(
    onNewPlayer: () -> Unit,
    onExistingPlayer: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.wel_back),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "BugsGame",
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold,
                color = Green
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Добро пожаловать!",
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                color = Green
            )
            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = "Выберите, как хотите продолжить",
                fontSize = 18.sp,
                color = Green
            )
            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = onNewPlayer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Новый игрок",
                    fontSize = 22.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onExistingPlayer,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(
                    1.7.dp,
                    MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Существующий игрок",
                    fontSize = 22.sp,
                    color = Green
                )
            }
        }
    }
}
