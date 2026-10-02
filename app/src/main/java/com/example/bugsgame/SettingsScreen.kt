package com.example.bugsgame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bugsgame.ui.theme.Green
import androidx.compose.ui.platform.LocalContext

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var gameSpeed by rememberSaveable {
        mutableFloatStateOf(GameSettings.getGameSpeed(context))
    }
    var maxBeetles by rememberSaveable {
        mutableFloatStateOf(GameSettings.getMaxBeetles(context))
    }
    var bonusInterval by rememberSaveable {
        mutableFloatStateOf(GameSettings.getBonusInterval(context))
    }
    var roundDuration by rememberSaveable {
        mutableFloatStateOf(GameSettings.getRoundDuration(context))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "${stringResource(R.string.game_speed)}: ${gameSpeed.toInt()}",
            fontSize = 18.sp,
            color = Green
        )

        Spacer(modifier = Modifier.height(10.dp))
        Slider(
            value = gameSpeed,
            onValueChange = {
                gameSpeed = it
                GameSettings.saveGameSpeed(context, it)
            },
            valueRange = 1f..5f,
            steps = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "${stringResource(R.string.max_num_beetles)}: ${maxBeetles.toInt()}",
            fontSize = 18.sp,
            color = Green
        )

        Spacer(modifier = Modifier.height(10.dp))
        Slider(
            value = maxBeetles,
            onValueChange = {
                maxBeetles = it
                GameSettings.saveMaxBeetles(context, it)
            },
            valueRange = 10f..30f,
            steps = 19,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "${stringResource(R.string.bonus_interval)}: ${bonusInterval.toInt()} (сек)",
            fontSize = 18.sp,
            color = Green
        )

        Spacer(modifier = Modifier.height(10.dp))
        Slider(
            value = bonusInterval,
            onValueChange = {
                bonusInterval = it
                GameSettings.saveBonusInterval(context, it)
            },
            valueRange = 20f..40f,
            steps = 18,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "${stringResource(R.string.round_duration)}: ${roundDuration.toInt()} (мин)",
            fontSize = 18.sp,
            color = Green
        )
        Spacer(modifier = Modifier.height(10.dp))
        Slider(
            value = roundDuration,
            onValueChange = {
                roundDuration = it
                GameSettings.saveRoundDuration(context, it)
            },
            valueRange = 1f..5f,
            steps = 3,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

