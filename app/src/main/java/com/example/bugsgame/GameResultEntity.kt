package com.example.bugsgame

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_results")
data class GameResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val playerId: Int,
    val score: Int,
    val hits: Int,
    val misses: Int,
    val accuracy: Float,
    val gameSpeed: Int,
    val date: Long = System.currentTimeMillis()
)
