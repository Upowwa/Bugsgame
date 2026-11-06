package com.example.bugsgame

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        PlayerEntity::class,
        GameResultEntity::class,
        ShopItemEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao
    abstract fun gameResultDao(): GameResultDao
    abstract fun shopDao(): ShopDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bugsgame_database"
                )
                    .addMigrations(
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5
                    )
                    .build().also { INSTANCE = it }
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "alter table players add column coins integer not null default 0"
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """create table if not exists shop_items (
                        playerId integer not null,
                        category text not null,
                        itemName text not null,
                        isOwned integer not null,
                        isEquipped integer not null,
                        primary key(playerId, category, itemName)
                    )
                    """.trimIndent()
                )
            }
        }
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "alter table shop_items add column price integer"
                )
            }
        }
    }

    suspend fun deletePlayerCompletely(player: PlayerEntity) {
        withTransaction {
            gameResultDao().deleteByPlayer(player.id)
            shopDao().deleteByPlayer(player.id)
            playerDao().delete(player)
        }
    }

    suspend fun purchaseShopItem(
        playerId: Int,
        category: String,
        itemName: String,
        price: Int
    ): Boolean = withTransaction {

        val existingItem = shopDao().getItem(
            playerId,
            category,
            itemName
        )

        if (existingItem?.isOwned == true) {
            return@withTransaction false
        }

        val updatedRows = playerDao().spendCoins(playerId, price)

        if (updatedRows == 0) {
            return@withTransaction false
        }

        shopDao().insertItem(
            ShopItemEntity(
                playerId = playerId,
                category = category,
                itemName = itemName,
                isOwned = true,
                isEquipped = false,
                price = price
            )
        )

        shopDao().equipItem(
            playerId = playerId,
            category = category,
            itemName = itemName
        )

        true
    }
}
