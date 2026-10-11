package com.example.bugsgame

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameResultDao {
    @Insert
    suspend fun insert(result: GameResultEntity): Long

    @Query("""select * from game_results order by score desc""")
    suspend fun getAll(): List<GameResultEntity>

    @Query("""select * from game_results where playerId = :playerId order by score desc""")
    suspend fun getByPlayer(playerId: Int): List<GameResultEntity>

    @Query("""select * from game_results order by score desc limit 10""")
    suspend fun getTop10(): List<GameResultEntity>

    @Query("delete from game_results where playerId = :playerId")
    suspend fun deleteByPlayer(playerId: Int)
}
