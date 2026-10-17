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

    @Query("""
    select * from game_results as g
    where g.gameSpeed = :speed
      and g.score = (
          select max(r.score)
          from game_results as r
          where r.playerId = g.playerId
            and r.gameSpeed = g.gameSpeed
      )
      and g.accuracy = (
          select MAX(r.accuracy)
          from game_results as r
          where r.playerId = g.playerId
            and r.gameSpeed = g.gameSpeed
            and r.score = g.score
      )
      and g.date = (
          select MIN(r.date)
          from game_results as r
          where r.playerId = g.playerId
            and r.gameSpeed = g.gameSpeed
            and r.score = g.score
            and r.accuracy = g.accuracy
      )
    order by g.score desc, g.accuracy desc, g.date asc limit 5
    """)
    suspend fun getTop5BySpeed(speed: Int): List<GameResultEntity>
}
