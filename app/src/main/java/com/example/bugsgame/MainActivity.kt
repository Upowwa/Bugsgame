package com.example.bugsgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val composeView = findViewById<ComposeView>(
            R.id.composeView
        )

        composeView.setContent {
            MainScreen()
        }
    }
}