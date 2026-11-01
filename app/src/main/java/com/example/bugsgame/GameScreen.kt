package com.example.bugsgame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bugsgame.ui.theme.Green
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableLongStateOf

class GameSessionState(
    initialSeconds: Int
) {
    val score = mutableIntStateOf(0)
    val bonusScore = mutableIntStateOf(0)
    val hits = mutableIntStateOf(0)
    val misses = mutableIntStateOf(0)
    val remainingSeconds = mutableIntStateOf(initialSeconds)
    val bonusRemainingMillis = mutableLongStateOf(0L)
    val goldBugRemainingMillis = mutableLongStateOf(20_000L)
    var tiltRemainingMillis = mutableLongStateOf(5_000L)
    val bugs = mutableStateListOf<Bug>()
    val fallingBugIds = mutableStateListOf<Int>()
    val resetVersion = mutableIntStateOf(0)

    val bonusVisible = mutableStateOf(false)
    val bonusX = mutableFloatStateOf(0.5f)
    val bonusY = mutableFloatStateOf(-0.15f)
    val tiltActive = mutableStateOf(false)
    val tiltX = mutableFloatStateOf(0f)
    val tiltY = mutableFloatStateOf(0f)

    val goldBugVisible = mutableStateOf(false)
    val goldBugX = mutableFloatStateOf(0.5f)
    val goldBugY = mutableFloatStateOf(0.5f)
    val goldPrice = mutableFloatStateOf(0f)
    val goldBugDx = mutableFloatStateOf(1f)
    val goldBugDy = mutableFloatStateOf(1f)

    fun reset(initialSeconds: Int) {
        score.intValue = 0
        bonusScore.intValue = 0
        hits.intValue = 0
        misses.intValue = 0
        remainingSeconds.intValue = initialSeconds
        bugs.clear()
        fallingBugIds.clear()
        bonusVisible.value = false
        bonusX.floatValue = 0.5f
        bonusY.floatValue = -0.15f
        tiltActive.value = false
        tiltX.floatValue = 0f
        tiltY.floatValue = 0f
        goldBugVisible.value = false
        goldBugX.floatValue = 0.5f
        goldBugY.floatValue = 0.5f
        goldBugDx.floatValue = 1f
        goldBugDy.floatValue = 1f
        resetVersion.intValue++
        bonusRemainingMillis.longValue = 0L
        goldBugRemainingMillis.longValue = 20_000L
        tiltRemainingMillis.longValue = 5_000L
    }
}

private enum class GameStatus { READY, PLAYING, PAUSED, FINISHED }

@Composable
fun GameScreen(
    playerId: Int,
    onGameActiveChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    var activeRoundDuration by remember { mutableIntStateOf(GameSettings.getRoundDuration(context).toInt()) }
    var activeGameSpeed by remember { mutableIntStateOf(GameSettings.getGameSpeed(context).toInt()) }
    var activeMaxBeetles by remember { mutableIntStateOf(GameSettings.getMaxBeetles(context).toInt()) }
    var activeBonusInterval by remember { mutableIntStateOf(GameSettings.getBonusInterval(context).toInt()) }
    val gameSessionViewModel: GameSessionViewModel = koinViewModel()
    val session = gameSessionViewModel.session
    gameSessionViewModel.configureInitialDuration(activeRoundDuration * 60)
    var status by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(GameStatus.READY) }

    fun startNewRound() {
        val newRoundDuration = GameSettings.getRoundDuration(context).toInt()

        activeRoundDuration = newRoundDuration
        activeGameSpeed = GameSettings.getGameSpeed(context).toInt()
        activeMaxBeetles = GameSettings.getMaxBeetles(context).toInt()
        activeBonusInterval = GameSettings.getBonusInterval(context).toInt()

        session.reset(newRoundDuration * 60)
        status = GameStatus.PLAYING
    }

    LaunchedEffect(status) {
        onGameActiveChange(status == GameStatus.PLAYING)
    }

    LaunchedEffect(status) {
        if (status == GameStatus.FINISHED) {
            val hits = session.hits.intValue
            val misses = session.misses.intValue
            val accuracy = if (hits + misses > 0) {
                hits.toFloat() / (hits + misses) * 100f
            } else {
                0f
            }
            database.gameResultDao().insert(
                GameResultEntity(
                    playerId = playerId,
                    score = session.score.intValue + session.bonusScore.intValue,
                    hits = hits,
                    misses = misses,
                    accuracy = accuracy,
                    gameSpeed = activeGameSpeed
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (status != GameStatus.READY) {
            GamePlayScreen(
                state = session,
                isPlaying = status == GameStatus.PLAYING,
                gameSpeed = activeGameSpeed.toFloat(),
                maxBeetles = activeMaxBeetles,
                bonusInterval = activeBonusInterval,
                onTimeExpired = {
                    if (status == GameStatus.PLAYING) {
                        status = GameStatus.FINISHED
                    }
                },
                onPause = {
                    if (status == GameStatus.PLAYING) {
                        status = GameStatus.PAUSED
                    }
                }
            )
        }

        when (status) {
            GameStatus.READY -> {
                Image(
                    painter = painterResource(R.drawable.back_game),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(50.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "BugsGame",
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Лови жуков, набирай очки и побей свой рекорд!",
                            fontSize = 25.sp,
                            modifier = Modifier.fillMaxWidth(),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(60.dp))

                        Text(
                            text = "Нажмите, чтобы начать",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(15.dp))

                        Button(
                            onClick = { startNewRound() },
                            contentPadding = PaddingValues(
                                horizontal = 40.dp,
                                vertical = 10.dp
                            )
                        ) {
                            Text(
                                text = "Начать игру",
                                fontSize = 27.sp
                            )
                        }
                    }
                }
            }

            GameStatus.PLAYING -> { }

            GameStatus.PAUSED -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.width(270.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Пауза",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(80.dp))

                        Button(
                            onClick = { status = GameStatus.PLAYING },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Продолжить",
                                fontSize = 27.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(15.dp))

                        Button(
                            onClick = { status = GameStatus.FINISHED },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Завершить игру",
                                fontSize = 27.sp
                            )
                        }
                    }
                }
            }

            GameStatus.FINISHED -> {
                Image(
                    painter = painterResource(R.drawable.back_gameover2),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 45.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Игра окончена!",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Green
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        text = "очки: ${session.score.intValue} + ${session.bonusScore.intValue}",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Green
                    )
                    Text(
                        text = "попадания: ${session.hits.intValue}",
                        fontSize = 25.sp,
                        color = Green
                    )
                    Text(
                        text = "промахи: ${session.misses.intValue}",
                        fontSize = 25.sp,
                        color = Green
                    )
                    val hits = session.hits.intValue
                    val misses = session.misses.intValue
                    val accuracy = if (hits + misses > 0) {
                        hits.toFloat() / (hits + misses) * 100f
                    } else {
                        0f
                    }
                    Text(
                        String.format(
                            Locale.current.platformLocale,
                            "точность: %.1f%%",
                            accuracy
                        ),
                        fontSize = 25.sp,
                        color = Green
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        text = "Продолжить?",
                        fontSize = 20.sp,
                        color = Green
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                session.reset(activeRoundDuration * 60)
                                status = GameStatus.PLAYING
                            },
                            contentPadding = PaddingValues(
                                horizontal = 30.dp,
                                vertical = 10.dp
                            )
                        ) {
                            Text(
                                text = "Да",
                                fontSize = 25.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(25.dp))

                        OutlinedButton(
                            onClick = {
                                status = GameStatus.READY
                            },
                            contentPadding = PaddingValues(
                                horizontal = 30.dp,
                                vertical = 10.dp
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = "Нет",
                                fontSize = 25.sp,
                                color = Green
                            )
                        }
                    }
                }
            }
        }
    }
}
