package com.example.bugsgame

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

private enum class AppScreen {
    WELCOME,
    REGISTRATION,
    PLAYER_SELECTION,
    MAIN
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf(AppScreen.WELCOME) }
    var selectedPlayer by remember { mutableStateOf<Player?>(null) }

    when (currentScreen) {
        AppScreen.WELCOME -> {
            WelcomeScreen(
                onNewPlayer = { currentScreen = AppScreen.REGISTRATION },
                onExistingPlayer = { currentScreen = AppScreen.PLAYER_SELECTION }
            )
        }

        AppScreen.REGISTRATION -> {
            RegistrationScreen(
                onPlayerSelected = { player ->
                    selectedPlayer = player
                    currentScreen = AppScreen.MAIN
                },
                onBack = { currentScreen = AppScreen.WELCOME }
            )
        }

        AppScreen.PLAYER_SELECTION -> {
            PlayerSelectionScreen(
                onPlayerSelected = { player ->
                    selectedPlayer = player
                    currentScreen = AppScreen.MAIN
                },
                onBack = { currentScreen = AppScreen.WELCOME }
            )
        }

        AppScreen.MAIN -> {
            selectedPlayer?.let { player ->
                MainScreen(
                    player = player,
                    onChangePlayer = {
                        selectedPlayer = null
                        currentScreen = AppScreen.WELCOME
                    }
                )
            }
        }
    }
}
