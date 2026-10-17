package com.example.bugsgame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bugsgame.ui.theme.Green
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.Box

private data class RecordRow(
    val playerName: String,
    val score: Int
)

@Composable
fun RecordsScreen() {
    val context = LocalContext.current

    var selectedSpeed by remember { mutableIntStateOf(1) }
    var records by remember { mutableStateOf<List<RecordRow>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedSpeed) {
        isLoading = true
        errorMessage = null
        try {
            val database = AppDatabase.getDatabase(context)
            val results = database.gameResultDao().getTop5BySpeed(selectedSpeed)

            records = results.mapNotNull { result ->
                val player = database.playerDao().getById(result.playerId)

                player?.let {
                    RecordRow(
                        playerName = it.fullName,
                        score = result.score
                    )
                }
            }
        } catch (e: Exception) {
            records = emptyList()
            errorMessage = "Не удалось загрузить рекорды"
        } finally {
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.records_back2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon_rec),
                    contentDescription = "Кубок",
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Рекорды",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Лучшие результаты игроков",
                        fontSize = 15.sp,
                        color = Green
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Spacer(modifier = Modifier.padding(15.dp))
            Text(
                text = "Скорость игры",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.padding(3.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                (1..5).forEach { speed ->
                    Button(
                        onClick = { selectedSpeed = speed },
                        modifier = Modifier.weight(1f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 0.dp,
                            vertical = 8.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedSpeed == speed) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            contentColor = if (selectedSpeed == speed) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    ) {
                        Text(
                            text = "$speed",
                            fontSize = 18.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(10.dp))

            Text(
                text = "Топ-5 · скорость $selectedSpeed",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(10.dp))

            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                errorMessage != null -> {
                    Text(text = errorMessage!!)
                }
                records.isEmpty() -> {
                    Text(
                        text = "Пока нет результатов на этой скорости.",
                        fontSize = 18.sp,
                        color = Green
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(records) { index, record ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        MaterialTheme.shapes.medium
                                    )
                                    .padding(15.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                when (index) {
                                    0 -> Image(
                                        painter = painterResource(id = R.drawable.first),
                                        contentDescription = "Первое место",
                                        modifier = Modifier.size(30.dp)
                                    )
                                    1 -> Image(
                                        painter = painterResource(id = R.drawable.second),
                                        contentDescription = "Второе место",
                                        modifier = Modifier.size(30.dp)
                                    )
                                    2 -> Image(
                                        painter = painterResource(id = R.drawable.third),
                                        contentDescription = "Третье место",
                                        modifier = Modifier.size(30.dp)
                                    )
                                    else -> Text(
                                        text = "  ${index + 1}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.width(30.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(20.dp))

                                Text(
                                    text = record.playerName,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 20.sp,
                                    color = Green
                                )
                                Text(
                                    text = "${record.score}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
