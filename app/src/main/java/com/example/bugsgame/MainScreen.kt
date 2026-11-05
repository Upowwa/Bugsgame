package com.example.bugsgame

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.content.pm.ActivityInfo
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun MainScreen(player: Player, onChangePlayer: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 5 })
    val coroutineScope = rememberCoroutineScope()
    var isGameActive by rememberSaveable { mutableStateOf(false) }
    val activity = LocalActivity.current

    LaunchedEffect(isGameActive) {
        activity?.requestedOrientation = if (isGameActive) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    val tabs = listOf(
        Pair(R.drawable.registration, R.drawable.registration_selected),
        Pair(R.drawable.store, R.drawable.store_selected),
        Pair(R.drawable.game, R.drawable.game_selected),
        Pair(R.drawable.records, R.drawable.records_selected),
        Pair(R.drawable.authors, R.drawable.authors_selected)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        if (!isGameActive) {
            TabRow(selectedTabIndex = pagerState.currentPage) {
                tabs.forEachIndexed { index, icons ->
                    val icon = if (pagerState.currentPage == index) {
                        icons.second
                    } else {
                        icons.first
                    }
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        icon = {
                            Image(
                                painter = painterResource(icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(vertical = 7.dp)
                                    .width(30.dp)
                                    .height(30.dp)
                            )
                        }
                    )
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 5,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            userScrollEnabled = !isGameActive
        ) { page ->
            when (page) {
                0 -> PlayerInfoScreen(
                    player = player,
                    onChangePlayer = onChangePlayer
                )
                1 -> ShopScreen(playerId = player.id)
                2 -> GameScreen(
                    playerId = player.id,
                    onGameActiveChange = { active -> isGameActive = active }
                )
                3 -> RecordsScreen()
                4 -> AuthorsScreen()
            }
        }
    }
}
