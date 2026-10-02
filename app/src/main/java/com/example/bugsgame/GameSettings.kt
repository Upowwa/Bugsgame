package com.example.bugsgame

import android.content.Context
import androidx.core.content.edit

object GameSettings {
    private const val PREFS_NAME = "game_settings"
    private const val GAME_SPEED = "game_speed"
    private const val MAX_BEETLES = "max_beetles"
    private const val BONUS_INTERVAL = "bonus_interval"
    private const val ROUND_DURATION = "round_duration"

    fun saveGameSpeed(context: Context, value: Float) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putFloat(GAME_SPEED, value)
            }
    }
    fun getGameSpeed(context: Context): Float {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getFloat(GAME_SPEED, 5f)
    }

    fun saveMaxBeetles(context: Context, value: Float) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putFloat(MAX_BEETLES, value)
            }
    }

    fun getMaxBeetles(context: Context): Float {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getFloat(MAX_BEETLES, 10f)
    }

    fun saveBonusInterval(context: Context, value: Float) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putFloat(BONUS_INTERVAL, value)
            }
    }

    fun getBonusInterval(context: Context): Float {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getFloat(BONUS_INTERVAL, 40f)
    }

    fun saveRoundDuration(context: Context, value: Float) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putFloat(ROUND_DURATION, value)
            }
    }

    fun getRoundDuration(context: Context): Float {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getFloat(ROUND_DURATION, 3f)
    }
}