package com.example.bugsgame

import androidx.lifecycle.ViewModel

class GameSessionViewModel : ViewModel() {
    val session = GameSessionState(initialSeconds = 60)

    private var initialDurationConfigured = false

    fun configureInitialDuration(initialSeconds: Int) {
        if (!initialDurationConfigured) {
            session.remainingSeconds.intValue = initialSeconds
            initialDurationConfigured = true
        }
    }
}
