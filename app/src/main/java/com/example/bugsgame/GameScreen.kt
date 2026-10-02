package com.example.bugsgame

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.offset
@Composable
fun GameScreen() {
    val context = LocalContext.current
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

    var remainingSeconds by remember { mutableIntStateOf(roundDuration * 60) }
    var gameActive by remember { mutableStateOf(true) }
    val bugs = remember { mutableStateListOf<Bug>() }

    fun createBugs() {
        bugs.clear()
        repeat(maxBeetles) { index ->
            val bugType: BugType
            val points: Int
            val imageRes: Int
            val randomType = Random.nextInt(100)
            when {
                randomType < 60 -> {
                    bugType = BugType.NORMAL
                    points = 1
                    imageRes = R.drawable.bug1
                }
                randomType < 90 -> {
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
                    dx = if (Random.nextBoolean()) { 1f } else { -1f },
                    dy = if (Random.nextBoolean()) { 1f } else { -1f }
                )
            )
        }
    }

    LaunchedEffect(Unit) { createBugs() }

    LaunchedEffect(gameActive) {
        if (gameActive) {
            while (remainingSeconds > 0) {
                delay(1000.milliseconds)
                remainingSeconds--
            }
            gameActive = false
        }
    }

    LaunchedEffect(gameActive) {
        while (gameActive) {
            delay(30.milliseconds)
            bugs.forEachIndexed { index, bug ->
                val movement = bug.speed * 2f
                var newX = bug.x + bug.dx * movement
                var newY = bug.y + bug.dy * movement
                var newDx = bug.dx
                var newDy = bug.dy

                if (newX < 0f || newX > logicalFieldSize - logicalBugSize) {
                    newX = newX.coerceIn(0f, logicalFieldSize - logicalBugSize)
                    newDx = -bug.dx
                }

                if (newY < 0f || newY > logicalFieldSize - logicalBugSize) {
                    newY = newY.coerceIn(0f, logicalFieldSize - logicalBugSize)
                    newDy = -bug.dy
                }
                bugs[index] = bug.copy(x = newX, y = newY, dx = newDx, dy = newDy)
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
                    .padding(start = 30.dp, top = 15.dp, end = 30.dp, bottom = 15.dp),
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
                        onClick = {
                            misses++
                            score--
                        }
                    )
            ) {
                val screenWidth = maxWidth
                val screenHeight = maxHeight

                fun logicalToScreenX(
                    x: Float
                ): Float {
                    return with(density) {
                        x / logicalFieldSize * screenWidth.toPx()
                    }
                }

                fun logicalToScreenY(
                    y: Float
                ): Float {
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
                                IntOffset(logicalToScreenX(bug.x).roundToInt(),
                                    logicalToScreenY(bug.y).roundToInt()
                                )
                            }
                            .clickable(
                                enabled = gameActive,
                                onClick = {
                                    hits++
                                    score += bug.points
                                    val index = bugs.indexOfFirst { it.id == bug.id }

                                    if (index != -1) {
                                        bugs[index] =
                                            bug.copy(
                                                x = Random.nextFloat() * (logicalFieldSize - logicalBugSize),
                                                y = Random.nextFloat() * (logicalFieldSize - logicalBugSize)
                                            )
                                    }
                                }
                            )
                    )
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

                    val accuracy =
                        if (hits + misses > 0) {
                            hits.toFloat() / (hits + misses) * 100
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
                            gameActive = true
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

