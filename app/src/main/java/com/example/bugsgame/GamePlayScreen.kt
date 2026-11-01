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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bugsgame.data.GoldPriceRepository
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private fun playBeetleScream(context: Context) {
    try {
        MediaPlayer.create(context, R.raw.beetles_scream)?.let { player ->
            player.setOnCompletionListener { it.release() }
            player.start()
        }
    } catch (_: Exception) {
    }
}

@Composable
fun GamePlayScreen(
    state: GameSessionState,
    isPlaying: Boolean,
    gameSpeed: Float,
    maxBeetles: Int,
    bonusInterval: Int,
    onTimeExpired: () -> Unit,
    onPause: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val logicalFieldSize = 1000f
    val bugSize = 50.dp
    val logicalBugSize = 50f

    var score by state.score
    var bonusScore by state.bonusScore
    var hits by state.hits
    var misses by state.misses
    var remainingSeconds by state.remainingSeconds

    var bonusVisible by state.bonusVisible
    var bonusX by state.bonusX
    var bonusY by state.bonusY

    var tiltActive by state.tiltActive
    var tiltX by state.tiltX
    var tiltY by state.tiltY

    var goldBugVisible by state.goldBugVisible
    var goldBugX by state.goldBugX
    var goldBugY by state.goldBugY
    var goldPrice by state.goldPrice
    var goldBugDx by state.goldBugDx
    var goldBugDy by state.goldBugDy

    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    val bugs = state.bugs
    val fallingBugIds = state.fallingBugIds

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

    LaunchedEffect(state.resetVersion.intValue) { createBugs() }
    LaunchedEffect(Unit) {
        val repository = GoldPriceRepository(context)
        val price = repository.refreshPrice() ?: repository.getSavedPrice()
        goldPrice = price?.price?.toFloat() ?: 0f
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (remainingSeconds > 0) {
                delay(1.seconds)
                remainingSeconds--
            }
            onTimeExpired()
        }
    }

    LaunchedEffect(isPlaying, bonusInterval) {
        if (isPlaying) {
            while (true) {
                if (!bonusVisible) {
                    if (state.bonusRemainingMillis.longValue <= 0L) {
                        state.bonusRemainingMillis.longValue = bonusInterval * 1000L
                    }
                    while (state.bonusRemainingMillis.longValue > 0L) {
                        val step = minOf(
                            100L,
                            state.bonusRemainingMillis.longValue
                        )
                        delay(step.milliseconds)

                        state.bonusRemainingMillis.longValue -= step
                    }
                    if (!tiltActive) {
                        bonusX = Random.nextFloat() * 0.7f + 0.15f
                        bonusY = -0.15f
                        bonusVisible = true
                        state.bonusRemainingMillis.longValue = bonusInterval * 1000L
                    } else {
                        state.bonusRemainingMillis.longValue = bonusInterval * 1000L
                        continue
                    }
                }
                while (bonusVisible && bonusY < 1.15f) {
                    withFrameNanos { }
                    bonusY += 0.003f
                }
                bonusVisible = false
            }
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            if (!goldBugVisible) {
                while (state.goldBugRemainingMillis.longValue > 0L) {
                    val step = minOf(
                        100L,
                        state.goldBugRemainingMillis.longValue
                    )
                    delay(step.milliseconds)
                    state.goldBugRemainingMillis.longValue -= step
                }

                goldBugX = Random.nextFloat() * (logicalFieldSize - logicalBugSize)
                goldBugY = Random.nextFloat() * (logicalFieldSize - logicalBugSize)

                goldBugDx = if (Random.nextBoolean()) 1f else -1f
                goldBugDy = if (Random.nextBoolean()) 1f else -1f

                goldBugVisible = true
                state.goldBugRemainingMillis.longValue = 5_000L
            }

            while (goldBugVisible &&
                state.goldBugRemainingMillis.longValue > 0L
            ) {
                val step = minOf(
                    100L,
                    state.goldBugRemainingMillis.longValue
                )
                delay(step.milliseconds)
                state.goldBugRemainingMillis.longValue -= step
            }
            if (goldBugVisible &&
                state.goldBugRemainingMillis.longValue <= 0L
            ) {
                goldBugVisible = false
            }
            if (!goldBugVisible) {
                state.goldBugRemainingMillis.longValue = 20_000L
            }
        }
    }

    LaunchedEffect(goldBugVisible, isPlaying) {
        var previousFrameTime = 0L
        while (goldBugVisible && isPlaying) {
            val frameTime = withFrameNanos { it }
            val deltaSeconds = if (previousFrameTime == 0L) {
                0f
            } else {
                ((frameTime - previousFrameTime) / 1_000_000_000f).coerceIn(0f, 0.05f)
            }

            previousFrameTime = frameTime

            val frameScale = deltaSeconds / 0.03f
            val movement = gameSpeed * 2f * frameScale

            var newX = goldBugX + goldBugDx * movement
            var newY = goldBugY + goldBugDy * movement

            if (newX < 0f || newX > logicalFieldSize - logicalBugSize) {
                goldBugDx = -goldBugDx
                newX = newX.coerceIn(0f, logicalFieldSize - logicalBugSize)
            }

            if (newY < 0f || newY > logicalFieldSize - logicalBugSize) {
                goldBugDy = -goldBugDy
                newY = newY.coerceIn(0f, logicalFieldSize - logicalBugSize)
            }

            goldBugX = newX
            goldBugY = newY
        }
    }

    DisposableEffect(tiltActive, isPlaying, accelerometer) {
        if (tiltActive && isPlaying && accelerometer != null) {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    tiltX = (-event.values[0] / SensorManager.GRAVITY_EARTH).coerceIn(-1f, 1f)
                    tiltY = (event.values[1] / SensorManager.GRAVITY_EARTH).coerceIn(-1f, 1f)
                }

                override fun onAccuracyChanged(
                    sensor: Sensor?,
                    accuracy: Int
                ) = Unit
            }
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
            onDispose { sensorManager.unregisterListener(listener) }
        } else {
            onDispose {}
        }
    }

    LaunchedEffect(tiltActive, isPlaying) {
        if (tiltActive && isPlaying) {
            while (state.tiltRemainingMillis.longValue > 0L) {
                val step = minOf(
                    100L,
                    state.tiltRemainingMillis.longValue
                )

                delay(step.milliseconds)
                state.tiltRemainingMillis.longValue -= step
            }
            tiltActive = false
            tiltX = 0f
            tiltY = 0f
            fallingBugIds.clear()
        }
    }

    LaunchedEffect(isPlaying) {
        var previousFrameTime = 0L
        while (isPlaying) {
            val frameTime = withFrameNanos { it }
            val deltaSeconds = if (previousFrameTime == 0L) {
                0f
            } else {
                ((frameTime - previousFrameTime) / 1_000_000_000f).coerceIn(0f, 0.05f)
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
                    val newX = (bug.x + tiltX * fallSpeed).coerceIn(0f, logicalFieldSize - logicalBugSize)
                    val newY = (bug.y + tiltY * fallSpeed).coerceIn(0f, logicalFieldSize - logicalBugSize)

                    bugs[index] = bug.copy(
                        x = newX,
                        y = newY
                    )
                    index++
                    continue
                }

                var newX = bug.x + (if (tiltActive) tiltX * 2f else bug.dx) * movement
                var newY = bug.y + (if (tiltActive) tiltY * 2f else bug.dy) * movement

                var newDx = bug.dx
                var newDy = bug.dy

                if (newX < 0f || newX > logicalFieldSize - logicalBugSize) {
                    newX = newX.coerceIn(
                        0f,
                        logicalFieldSize - logicalBugSize
                    )
                    if (!tiltActive) {
                        newDx = -bug.dx
                    }
                }

                if (newY < 0f || newY > logicalFieldSize - logicalBugSize) {
                    newY = newY.coerceIn(
                        0f,
                        logicalFieldSize - logicalBugSize
                    )
                    if (!tiltActive) {
                        newDy = -bug.dy
                    }
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
                    .padding(start = 30.dp, top = 25.dp, end = 30.dp, bottom = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "${remainingSeconds / 60}:${(remainingSeconds % 60).toString().padStart(2, '0')}",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.stop),
                        contentDescription = "Пауза",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { onPause() }
                    )
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "${score + bonusScore}",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            if (tiltActive && isPlaying) {
                Text(
                    text = "Наклоняй телефон!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable(
                        enabled = isPlaying,
                        interactionSource = remember { MutableInteractionSource() },
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
                    return with(density) { x / logicalFieldSize * screenWidth.toPx() }
                }

                fun logicalToScreenY(y: Float): Float {
                    return with(density) { y / logicalFieldSize * screenHeight.toPx() }
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
                                enabled = isPlaying,
                                interactionSource = remember {
                                    MutableInteractionSource()
                                },
                                indication = null,
                                onClick = {
                                    hits++
                                    score += bug.points

                                    val index = bugs.indexOfFirst { it.id == bug.id }
                                    if (index != -1) {
                                        fallingBugIds.remove(bug.id)
                                        bugs[index] = bug.copy(
                                            x = Random.nextFloat() * (logicalFieldSize - logicalBugSize),
                                            y = Random.nextFloat() * (logicalFieldSize - logicalBugSize)
                                        )
                                    }
                                }
                            )
                    )
                }
                if (goldBugVisible) {
                    Image(
                        painter = painterResource(R.drawable.gold_bug),
                        contentDescription = "Золотой таракан",
                        modifier = Modifier
                            .size(bugSize)
                            .offset {
                                IntOffset(
                                    logicalToScreenX(goldBugX).roundToInt(),
                                    logicalToScreenY(goldBugY).roundToInt()
                                )
                            }
                            .clickable(
                                enabled = isPlaying,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    goldBugVisible = false
                                    val bonusPoints = (goldPrice / 1000f).roundToInt()
                                    bonusScore += bonusPoints
                                    hits++
                                }
                            )
                    )
                }

                if (bonusVisible) {
                    Column(
                        modifier = Modifier.offset {
                            IntOffset(
                                x = with(density) { (screenWidth * bonusX - 60.dp).roundToPx() },
                                y = with(density) { (screenHeight * bonusY).roundToPx() }
                            )
                        }
                            .clickable(
                                enabled = isPlaying,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                bonusVisible = false
                                state.tiltRemainingMillis.longValue = 5_000L
                                tiltActive = true

                                bugs.forEach { fallingBugIds.add(it.id) }
                                playBeetleScream(context)
                            }
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.bonus),
                            contentDescription = "Бонус наклона",
                            modifier = Modifier.size(75.dp)
                        )
                    }
                }
            }
        }

    }
}
