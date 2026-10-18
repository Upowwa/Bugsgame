package com.example.bugsgame

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bugsgame.ui.theme.Green
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random
import androidx.compose.runtime.withFrameNanos


private fun playBeetleScream(context: Context) {
    try {
        MediaPlayer.create(context, R.raw.beetles_scream)?.let { player ->
            player.setOnCompletionListener { it.release() }
            player.start()
        }
    } catch (_: Exception) {
    }
}

private fun createReplacementBug(
    id: Int,
    gameSpeed: Float,
    logicalFieldSize: Float,
    logicalBugSize: Float
): Bug {
    val bugType: BugType
    val points: Int
    val imageRes: Int

    when (Random.nextInt(100)) {
        in 0..59 -> {
            bugType = BugType.NORMAL
            points = 1
            imageRes = R.drawable.bug1
        }
        in 60..89 -> {
            bugType = BugType.FAST
            points = 2
            imageRes = R.drawable.bug2
        }
        else -> {
            bugType = BugType.RARE
            points = 5
            imageRes = R.drawable.bug3
        }
    }

    return Bug(
        id = id,
        x = Random.nextFloat() * (logicalFieldSize - logicalBugSize),
        y = Random.nextFloat() * (logicalFieldSize - logicalBugSize),
        speed = gameSpeed,
        size = logicalBugSize,
        type = bugType,
        points = points,
        imageRes = imageRes,
        dx = if (Random.nextBoolean()) 1f else -1f,
        dy = if (Random.nextBoolean()) 1f else -1f
    )
}

@Composable
fun GameScreen(
    playerId: Int,
    onGameActiveChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val density = LocalDensity.current

    val gameSpeed = GameSettings.getGameSpeed(context)
    val maxBeetles = GameSettings.getMaxBeetles(context).toInt()
    val roundDuration = GameSettings.getRoundDuration(context).toInt()

    val logicalFieldSize = 1000f
    val bugSize = 50.dp
    val logicalBugSize = 50f

    var score by remember { mutableIntStateOf(0) }
    var hits by remember { mutableIntStateOf(0) }
    var misses by remember { mutableIntStateOf(0) }

    var remainingSeconds by remember {
        mutableIntStateOf(roundDuration * 60)
    }
    var gameActive by remember { mutableStateOf(true) }

    // Состояние бонуса и наклона телефона
    var bonusVisible by remember { mutableStateOf(false) }
    var bonusX by remember { mutableFloatStateOf(0.5f) }
    var bonusY by remember { mutableFloatStateOf(0.5f) }

    var tiltActive by remember { mutableStateOf(false) }
    var tiltX by remember { mutableFloatStateOf(0f) }
    var tiltY by remember { mutableFloatStateOf(0f) }

    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val accelerometer = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    LaunchedEffect(gameActive) {
        onGameActiveChange(gameActive)
    }

    LaunchedEffect(gameActive) {
        if (!gameActive) {
            val accuracy = if (hits + misses > 0) {
                hits.toFloat() / (hits + misses) * 100f
            } else {
                0f
            }

            database.gameResultDao().insert(
                GameResultEntity(
                    playerId = playerId,
                    score = score,
                    hits = hits,
                    misses = misses,
                    accuracy = accuracy,
                    gameSpeed = gameSpeed.toInt()
                )
            )
        }
    }

    val bugs = remember { mutableStateListOf<Bug>() }
    val fallingBugIds = remember { mutableStateListOf<Int>() }

    fun createBugs() {
        bugs.clear()
        fallingBugIds.clear()

        repeat(maxBeetles) { index ->
            val bugType: BugType
            val points: Int
            val imageRes: Int

            when (Random.nextInt(100)) {
                in 0..59 -> {
                    bugType = BugType.NORMAL
                    points = 1
                    imageRes = R.drawable.bug1
                }

                in 60..89 -> {
                    bugType = BugType.FAST
                    points = 2
                    imageRes = R.drawable.bug2
                }

                else -> {
                    bugType = BugType.RARE
                    points = 5
                    imageRes = R.drawable.bug3
                }
            }

            bugs.add(
                Bug(
                    id = index,
                    x = Random.nextFloat() * (logicalFieldSize - logicalBugSize),
                    y = Random.nextFloat() * (logicalFieldSize - logicalBugSize),
                    speed = gameSpeed,
                    size = logicalBugSize,
                    type = bugType,
                    points = points,
                    imageRes = imageRes,
                    dx = if (Random.nextBoolean()) 1f else -1f,
                    dy = if (Random.nextBoolean()) 1f else -1f
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        createBugs()
    }

    LaunchedEffect(gameActive) {
        if (gameActive) {
            while (remainingSeconds > 0 && gameActive) {
                delay(1000)
                remainingSeconds--
            }

            if (remainingSeconds <= 0) {
                gameActive = false
            }
        }
    }


    LaunchedEffect(gameActive) {
        while (gameActive) {
            delay(15_000)

            if (gameActive && !tiltActive) {
                bonusX = Random.nextFloat() * 0.7f + 0.15f
                bonusY = Random.nextFloat() * 0.7f + 0.15f
                bonusVisible = true

                delay(1_500)

                bonusVisible = false
            }
        }
    }
    DisposableEffect(tiltActive, gameActive, accelerometer) {
        if (tiltActive && gameActive && accelerometer != null) {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    tiltX = (-event.values[0] / SensorManager.GRAVITY_EARTH)
                        .coerceIn(-1f, 1f)

                    tiltY = (event.values[1] / SensorManager.GRAVITY_EARTH)
                        .coerceIn(-1f, 1f)
                }

                override fun onAccuracyChanged(
                    sensor: Sensor?,
                    accuracy: Int
                ) = Unit
            }

            sensorManager.registerListener(
                listener,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME
            )

            onDispose {
                sensorManager.unregisterListener(listener)
            }
        } else {
            onDispose {}
        }
    }

    LaunchedEffect(tiltActive) {
        if (tiltActive) {
            delay(8_000)
            tiltActive = false
            tiltX = 0f
            tiltY = 0f
        }
    }

    LaunchedEffect(gameActive) {
        var previousFrameTime = 0L

        while (gameActive) {
            val frameTime = withFrameNanos { it }

            val deltaSeconds = if (previousFrameTime == 0L) {
                0f
            } else {
                ((frameTime - previousFrameTime) / 1_000_000_000f)
                    .coerceIn(0f, 0.05f)
            }

            previousFrameTime = frameTime

            val frameScale = deltaSeconds / 0.03f

            var index = 0
            while (index < bugs.size) {
                val bug = bugs[index]
                val movement = bug.speed * 2f * frameScale

                val isFalling = fallingBugIds.contains(bug.id)

                if (isFalling) {
                    val fallSpeed = movement * 8f

                    val fallX = if (tiltActive) tiltX else 0f
                    val fallY = 1f

                    val newX = bug.x + fallX * fallSpeed
                    val newY = bug.y + fallY * fallSpeed

                    val hasLeftField =
                        newX < -logicalBugSize ||
                                newX > logicalFieldSize ||
                                newY < -logicalBugSize ||
                                newY > logicalFieldSize

                    if (hasLeftField) {
                        fallingBugIds.remove(bug.id)
                        bugs[index] = createReplacementBug(
                            bug.id,
                            gameSpeed,
                            logicalFieldSize,
                            logicalBugSize
                        )
                    } else {
                        bugs[index] = bug.copy(
                            x = newX,
                            y = newY,
                            dx = fallX,
                            dy = fallY
                        )
                    }

                    index++
                    continue
                }

                var newX = bug.x +
                        (if (tiltActive) tiltX * 2f else bug.dx) * movement

                var newY = bug.y +
                        (if (tiltActive) tiltY * 2f else bug.dy) * movement

                var newDx = bug.dx
                var newDy = bug.dy

                if (newX < 0f ||
                    newX > logicalFieldSize - logicalBugSize
                ) {
                    newX = newX.coerceIn(
                        0f,
                        logicalFieldSize - logicalBugSize
                    )
                    if (!tiltActive) newDx = -bug.dx
                }

                if (newY < 0f ||
                    newY > logicalFieldSize - logicalBugSize
                ) {
                    newY = newY.coerceIn(
                        0f,
                        logicalFieldSize - logicalBugSize
                    )
                    if (!tiltActive) newDy = -bug.dy
                }

                bugs[index] = bug.copy(
                    x = newX,
                    y = newY,
                    dx = newDx,
                    dy = newDy
                )

                index++
            }
        }
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.back_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 30.dp,
                        top = 15.dp,
                        end = 30.dp,
                        bottom = 15.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${remainingSeconds / 60}:${
                        (remainingSeconds % 60)
                            .toString()
                            .padStart(2, '0')
                    }",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "$score",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable(
                        enabled = gameActive,
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        indication = null,
                        onClick = {
                            misses++
                            score = (score - 1).coerceAtLeast(0)
                        }
                    )
            ) {
                val screenWidth = maxWidth
                val screenHeight = maxHeight

                fun logicalToScreenX(x: Float): Float {
                    return with(density) {
                        x / logicalFieldSize * screenWidth.toPx()
                    }
                }

                fun logicalToScreenY(y: Float): Float {
                    return with(density) {
                        y / logicalFieldSize * screenHeight.toPx()
                    }
                }

                bugs.forEach { bug ->
                    Image(
                        painter = painterResource(id = bug.imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(bugSize)
                            .offset {
                                IntOffset(
                                    logicalToScreenX(bug.x).roundToInt(),
                                    logicalToScreenY(bug.y).roundToInt()
                                )
                            }
                            .clickable(
                                enabled = gameActive,
                                interactionSource = remember {
                                    MutableInteractionSource()
                                },
                                indication = null,
                                onClick = {
                                    hits++
                                    score += bug.points

                                    val index =
                                        bugs.indexOfFirst { it.id == bug.id }

                                    if (index != -1) {
                                        fallingBugIds.remove(bug.id)
                                        bugs[index] = bug.copy(
                                            x = Random.nextFloat() *
                                                    (logicalFieldSize - logicalBugSize),
                                            y = Random.nextFloat() *
                                                    (logicalFieldSize - logicalBugSize)
                                        )
                                    }
                                }
                            )
                    )
                }

                if (bonusVisible && gameActive) {
                    Column(
                        modifier = Modifier
                            .offset(
                                x = screenWidth * bonusX - 60.dp,
                                y = screenHeight * bonusY - 70.dp
                            )
                            .clickable(
                                interactionSource = remember {
                                    MutableInteractionSource()
                                },
                                indication = null
                            ) {
                                bonusVisible = false
                                tiltActive = true

                                bugs.forEach { fallingBugIds.add(it.id) }
                                playBeetleScream(context)

                            }
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.bonus_tilt),
                            contentDescription = "Бонус наклона",
                            modifier = Modifier.size(90.dp)
                        )

                        Text(
                            text = "БОНУС!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Yellow
                        )

                        if (tiltActive) {
                            Text(
                                text = "Наклоняй телефон!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        if (!gameActive) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(R.drawable.back_gameover2),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = (-50).dp),
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
                        text = "очки: $score",
                        fontSize = 25.sp,
                        color = Green
                    )

                    Text(
                        text = "попадания: $hits",
                        fontSize = 25.sp,
                        color = Green
                    )

                    Text(
                        text = "промахи: $misses",
                        fontSize = 25.sp,
                        color = Green
                    )

                    val accuracy = if (hits + misses > 0) {
                        hits.toFloat() / (hits + misses) * 100f
                    } else {
                        0f
                    }

                    Text(
                        text = String.format(
                            Locale.current.platformLocale,
                            "точность: %.1f%%",
                            accuracy
                        ),
                        fontSize = 25.sp,
                        color = Green
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = {
                            score = 0
                            hits = 0
                            misses = 0
                            remainingSeconds = roundDuration * 60
                            bonusVisible = false
                            tiltActive = false
                            tiltX = 0f
                            tiltY = 0f
                            gameActive = true
                            fallingBugIds.clear()
                            createBugs()
                        }
                    ) {
                        Text(
                            text = "Играть снова",
                            fontSize = 30.sp
                        )
                    }
                }
            }
        }
    }
}