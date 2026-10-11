package com.example.bugsgame

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

@Dao
interface PlayerDao {
    @Insert
    suspend fun insert(player: PlayerEntity): Long

    @Query("select * from players order by fullName")
    suspend fun getAll(): List<PlayerEntity>

    @Query("select * from players where id = :id limit 1")
    suspend fun getById(id: Int): PlayerEntity?

    @Delete
    suspend fun delete(player: PlayerEntity)
}
