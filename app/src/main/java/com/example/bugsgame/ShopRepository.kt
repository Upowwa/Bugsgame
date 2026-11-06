package com.example.bugsgame

import kotlinx.coroutines.flow.Flow

class ShopRepository(
    private val database: AppDatabase
) {
    fun observeItems(playerId: Int): Flow<List<ShopItemEntity>> {
        return database.shopDao().observeItems(playerId)
    }

    suspend fun initializeShop(playerId: Int) {
        ShopCatalog.items.forEach { catalogItem ->
            database.shopDao().synchronizeItemPrice(
                playerId = playerId,
                category = catalogItem.category,
                itemName = catalogItem.item.name,
                currentPrice = catalogItem.item.price
            )
        }

        ShopCatalog.baseItems.forEach { (category, itemName) ->
            database.shopDao().ensureBaseItem(
                playerId = playerId,
                category = category,
                itemName = itemName
            )
        }
    }

    suspend fun purchase(
        playerId: Int,
        category: String,
        itemName: String,
        price: Int
    ): Boolean {
        return database.purchaseShopItem(
            playerId = playerId,
            category = category,
            itemName = itemName,
            price = price
        )
    }

    suspend fun equip(
        playerId: Int,
        category: String,
        itemName: String
    ) {
        database.shopDao().equipItem(
            playerId = playerId,
            category = category,
            itemName = itemName
        )
    }

    suspend fun unequip(
        playerId: Int,
        category: String
    ) {
        database.shopDao().clearEquipped(
            playerId = playerId,
            category = category
        )
    }
}
