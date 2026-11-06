package com.example.bugsgame

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    @Query("select * from shop_items where playerId = :playerId")
    fun observeItems(playerId: Int): Flow<List<ShopItemEntity>>

    @Query("delete from shop_items where playerId = :playerId")
    suspend fun deleteByPlayer(playerId: Int)

    @Query("""
        select * from shop_items
        where playerId = :playerId
        and category = :category
        and itemName = :itemName
        limit 1
    """)
    suspend fun getItem(
        playerId: Int,
        category: String,
        itemName: String
    ): ShopItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShopItemEntity)

    @Query("""
        update shop_items
        set isOwned = 1
        where playerId = :playerId
        and category = :category
        and itemName = :itemName
    """)
    suspend fun markAsOwned(
        playerId: Int,
        category: String,
        itemName: String
    )

    @Query("""
        update shop_items
        set isEquipped = 0
        where playerId = :playerId
        and category = :category
    """)
    suspend fun clearEquipped(
        playerId: Int,
        category: String
    )

    @Query("""
        update shop_items
        set isEquipped = 1
        where playerId = :playerId
        and category = :category
        and itemName = :itemName
        and isOwned = 1
    """)
    suspend fun setEquipped(
        playerId: Int,
        category: String,
        itemName: String
    )

    @Transaction
    suspend fun equipItem(
        playerId: Int,
        category: String,
        itemName: String
    ) {
        val item = getItem(playerId, category, itemName)

        if (item?.isOwned == true) {
            clearEquipped(playerId, category)
            setEquipped(playerId, category, itemName)
        }
    }

    @Query("""
        select exists(
            select 1 from shop_items
            where playerId = :playerId
            and category = :category
            and isEquipped = 1
        )
    """)
    suspend fun hasEquippedItem(playerId: Int, category: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItemIfAbsent(item: ShopItemEntity)

    @Query("""
        update shop_items
        set price = :price
        where playerId = :playerId
        and category = :category
        and itemName = :itemName
    """)
    suspend fun updateStoredPrice(
        playerId: Int,
        category: String,
        itemName: String,
        price: Int
    )

    @Query("""
        update shop_items
        set price = :price,
            isOwned = 0,
            isEquipped = 0
        where playerId = :playerId
        and category = :category
        and itemName = :itemName
    """)
    suspend fun updatePriceAndResetOwnership(
        playerId: Int,
        category: String,
        itemName: String,
        price: Int
    )

    @Transaction
    suspend fun synchronizeItemPrice(
        playerId: Int,
        category: String,
        itemName: String,
        currentPrice: Int
    ) {
        val existingItem = getItem(playerId, category, itemName)

        when {
            existingItem == null -> {
                insertItemIfAbsent(
                    ShopItemEntity(
                        playerId = playerId,
                        category = category,
                        itemName = itemName,
                        isOwned = false,
                        isEquipped = false,
                        price = currentPrice
                    )
                )
            }

            existingItem.price == null -> {
                updateStoredPrice(
                    playerId,
                    category,
                    itemName,
                    currentPrice
                )
            }

            existingItem.price != currentPrice -> {
                updatePriceAndResetOwnership(
                    playerId,
                    category,
                    itemName,
                    currentPrice
                )
            }
        }
    }

    @Transaction
    suspend fun ensureBaseItem(
        playerId: Int,
        category: String,
        itemName: String
    ) {
        val existingItem = getItem(playerId, category, itemName)

        if (existingItem == null) {
            insertItemIfAbsent(
                ShopItemEntity(
                    playerId = playerId,
                    category = category,
                    itemName = itemName,
                    isOwned = true,
                    isEquipped = !hasEquippedItem(playerId, category)
                )
            )
        } else {
            if (!existingItem.isOwned) {
                markAsOwned(playerId, category, itemName)
            }

            if (!hasEquippedItem(playerId, category)) {
                setEquipped(playerId, category, itemName)
            }
        }
    }
}