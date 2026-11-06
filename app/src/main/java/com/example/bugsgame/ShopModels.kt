package com.example.bugsgame

enum class ShopTab {
    INTERIOR,
    WARDROBE
}

data class ShopItem(
    val name: String,
    val price: Int,
    val image: Int,
    val owned: Boolean = false
)

object ShopCategories {
    const val FACE = "face"
    const val BACKGROUND = "background"
    const val TOP = "top"
    const val BOTTOM = "bottom"
    const val HATS = "hats"
    const val ACCESSORIES = "accessories"
    const val SHOES = "shoes"
}