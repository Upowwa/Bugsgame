package com.example.bugsgame

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun MainScreen(
    player: Player,
    onChangePlayer: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 5 })
    val coroutineScope = rememberCoroutineScope()
    var isGameActive by remember { mutableStateOf(true) }
    val isGamePageLocked = pagerState.currentPage == 2 && isGameActive

    val tabs = listOf(
        Pair(R.drawable.registration, R.drawable.registration_selected),
        Pair(R.drawable.rules, R.drawable.rules_selected),
        Pair(R.drawable.game, R.drawable.game_selected),
        Pair(R.drawable.authors, R.drawable.authors_selected),
        Pair(R.drawable.settings, R.drawable.settings_selected)
    )

    Column {
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
                        if (!isGamePageLocked) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    },
                    icon = {
                        Image(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(vertical = 7.dp)
                                .width(25.dp)
                                .height(25.dp)
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            userScrollEnabled = !isGamePageLocked
        ) { page ->
            when (page) {
                0 -> PlayerInfoScreen(
                    player = player,
                    onChangePlayer = onChangePlayer
                )
                1 -> RulesScreen()
                2 -> GameScreen(
                    playerId = player.id,
                    onGameActiveChange = { active ->
                        isGameActive = active
                    }
                )
                3 -> AuthorsScreen()
                4 -> SettingsScreen()
            }
        }
    }
}
