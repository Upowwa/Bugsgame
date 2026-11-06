package com.example.bugsgame

import androidx.room.Entity

@Entity(
    tableName = "shop_items",
    primaryKeys = ["playerId", "category", "itemName"]
)
data class ShopItemEntity(
    val playerId: Int,
    val category: String,
    val itemName: String,
    val isOwned: Boolean = false,
    val isEquipped: Boolean = false,
    val price: Int? = null
)