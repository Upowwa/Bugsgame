package com.example.bugsgame

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction

@Database(
    entities = [
        PlayerEntity::class,
        GameResultEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao
    abstract fun gameResultDao(): GameResultDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bugsgame_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
    suspend fun deletePlayerCompletely(player: PlayerEntity) {
        withTransaction {
            gameResultDao().deleteByPlayer(player.id)
            playerDao().delete(player)
        }
    }
}
