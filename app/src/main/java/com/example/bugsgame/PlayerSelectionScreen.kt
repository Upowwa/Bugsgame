package com.example.bugsgame

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.bugsgame.ui.theme.Green
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.IconButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.layout.ContentScale

@Composable
fun PlayerSelectionScreen(
    onPlayerSelected: (Player) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val coroutineScope = rememberCoroutineScope()
    var players by remember { mutableStateOf(emptyList<PlayerEntity>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        players = database.playerDao().getAll()
        isLoading = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.select_back),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(35.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Выбор игрока",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Green
            )
            Spacer(modifier = Modifier.height(25.dp))
            when {
                isLoading -> {
                    Text(
                        text = "Загрузка пользователей...",
                        fontSize = 19.sp,
                        color = Green
                    )
                }
                players.isEmpty() -> {
                    Text(
                        text = "Сохранённых пользователей пока нет",
                        fontSize = 19.sp,
                        color = Green
                    )
                }
                else -> {
                    players.forEach { savedPlayer ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    onPlayerSelected(
                                        Player(
                                            id = savedPlayer.id,
                                            fullName = savedPlayer.fullName,
                                            gender = savedPlayer.gender,
                                            course = savedPlayer.course,
                                            birthDate = savedPlayer.birthDate,
                                            zodiac = savedPlayer.zodiac
                                        )
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = savedPlayer.fullName,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 22.sp
                                )
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            database.deletePlayerCompletely(savedPlayer)
                                            players = database.playerDao().getAll()
                                        }
                                    }
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.delete),
                                        contentDescription = "Удалить игрока",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(
                    1.5.dp,
                    MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Назад",
                    color = Green,
                    fontSize = 22.sp
                )
            }
        }
    }
}
