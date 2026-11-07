package com.example.bugsgame

data class ShopCatalogItem(
    val category: String,
    val item: ShopItem
)

object ShopCatalog {
    val wardrobeCategories = listOf(
        ShopCategories.FACE to R.string.shop_category_face,
        ShopCategories.TOP to R.string.shop_category_top,
        ShopCategories.BOTTOM to R.string.shop_category_bottom,
        ShopCategories.HATS to R.string.shop_category_hats,
        ShopCategories.ACCESSORIES to R.string.shop_category_accessories,
        ShopCategories.SHOES to R.string.shop_category_shoes
    )
    val items = listOf(
        ShopCatalogItem(
            ShopCategories.BACKGROUND,
            ShopItem("Тропический пляж", 3, R.drawable.shop_beach)
        ),
        ShopCatalogItem(
            ShopCategories.BACKGROUND,
            ShopItem("Реперский зал", 1, R.drawable.shop_hall)
        ),
        ShopCatalogItem(
            ShopCategories.BACKGROUND,
            ShopItem("Уютный офис", 1, R.drawable.shop_office)
        ),
        ShopCatalogItem(
            ShopCategories.BACKGROUND,
            ShopItem("Помойка", 0, R.drawable.shop_yard)
        ),

        ShopCatalogItem(
            ShopCategories.FACE,
            ShopItem("Улыбка", 1, R.drawable.smile_icon)
        ),
        ShopCatalogItem(
            ShopCategories.FACE,
            ShopItem("Радость", 1, R.drawable.grin_icon)
        ),
        ShopCatalogItem(
            ShopCategories.FACE,
            ShopItem("Спокойствие", 1, R.drawable.neutral_icon)
        ),
        ShopCatalogItem(
            ShopCategories.FACE,
            ShopItem("Грусть", 0, R.drawable.sad_icon)
        ),

        ShopCatalogItem(
            ShopCategories.ACCESSORIES,
            ShopItem("Галстук", 1, R.drawable.shop_tie)
        ),
        ShopCatalogItem(
            ShopCategories.TOP,
            ShopItem("Синий полосатый пиджак", 1, R.drawable.blazer_icon)
        ),
        ShopCatalogItem(
            ShopCategories.BOTTOM,
            ShopItem("Офисная юбка", 1, R.drawable.office_skirt_icon)
        ),
        ShopCatalogItem(
            ShopCategories.BOTTOM,
            ShopItem("Праздничная юбка", 1, R.drawable.party_skirt_icon)
        ),
        ShopCatalogItem(
            ShopCategories.SHOES,
            ShopItem("Бордовые лоферы", 1, R.drawable.lofers_icon)
        )
    )

    val baseItems = listOf(
        ShopCategories.FACE to "Грусть",
        ShopCategories.BACKGROUND to "Помойка"
    )

    fun getItems(category: String): List<ShopItem> {
        return items
            .filter { it.category == category }
            .map { it.item }
    }

    fun findItem(category: String, itemName: String): ShopItem? {
        return items.find {
            it.category == category && it.item.name == itemName
        }?.item
    }
}
