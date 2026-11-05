package com.example.bugsgame

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

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

    @Query("update players set coins = coins + :amount where id = :playerId")
    suspend fun addCoins(playerId: Int, amount: Int)

    @Query("""update players set coins = coins - :price where id = :playerId and coins >= :price""")
    suspend fun spendCoins(playerId: Int, price: Int): Int

    @Query("select * from players where id = :playerId")
    fun observePlayer(playerId: Int): Flow<PlayerEntity?>
}
