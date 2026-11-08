package com.example.bugsgame

data class ShopCatalogItem(
    val category: String,
    val item: ShopItem,
    val previewImage: Int = item.image
)

object ShopCatalog {
    val wardrobeCategories = listOf(
        ShopCategories.FACE to R.string.shop_category_face,
        ShopCategories.TOP to R.string.shop_category_top,
        ShopCategories.BOTTOM to R.string.shop_category_bottom,
        ShopCategories.HATS to R.string.shop_category_hats,
        ShopCategories.ACCESSORIES to R.string.shop_category_accessories,
        ShopCategories.SHOES to R.string.shop_category_shoes,
        ShopCategories.OTHER to R.string.shop_category_other
    )
    val items = listOf(
        ShopCatalogItem(
            ShopCategories.BACKGROUND,
            ShopItem("Тропический пляж", 0, R.drawable.shop_beach)
        ),
        ShopCatalogItem(
            ShopCategories.BACKGROUND,
            ShopItem("Реперский зал", 0, R.drawable.shop_hall)
        ),
        ShopCatalogItem(
            ShopCategories.BACKGROUND,
            ShopItem("Уютный офис", 0, R.drawable.shop_office)
        ),
        ShopCatalogItem(
            ShopCategories.BACKGROUND,
            ShopItem("Помойка", 0, R.drawable.shop_yard)
        ),


        ShopCatalogItem(
            category = ShopCategories.FACE,
            item = ShopItem("Улыбка", 0, R.drawable.face_smile),
            previewImage = R.drawable.face_smile_icon
        ),
        ShopCatalogItem(
            category = ShopCategories.FACE,
            item = ShopItem("Радость", 0, R.drawable.face_grin),
            previewImage = R.drawable.face_grin_icon
        ),
        ShopCatalogItem(
            category = ShopCategories.FACE,
            item = ShopItem("Спокойствие", 0, R.drawable.face_neutral),
            previewImage = R.drawable.face_neutral_icon
        ),
        ShopCatalogItem(
            category = ShopCategories.FACE,
            item = ShopItem("Грусть", 0, R.drawable.face_sad),
            previewImage = R.drawable.face_sad_icon
        ),


        ShopCatalogItem(
            category = ShopCategories.TOP,
            item = ShopItem("Рубашка", 0, R.drawable.shirt),
            previewImage = R.drawable.shirt_icon
        ),
        ShopCatalogItem(
            category = ShopCategories.TOP,
            item = ShopItem("Пиджак", 0, R.drawable.jacket),
            previewImage = R.drawable.jacket_icon
        ),
        ShopCatalogItem(
            category = ShopCategories.TOP,
            item = ShopItem("Бомбер", 0, R.drawable.bomber),
            previewImage = R.drawable.bomber_icon
        ),

        ShopCatalogItem(
            category = ShopCategories.BOTTOM,
            item = ShopItem("Юбка", 0, R.drawable.skirt),
            previewImage = R.drawable.skirt_icon
        ),


        ShopCatalogItem(
            category = ShopCategories.HATS,
            item = ShopItem("Венок", 0, R.drawable.headband),
            previewImage = R.drawable.headband_icon
        ),
        ShopCatalogItem(
            category = ShopCategories.HATS,
            item = ShopItem("Кепка", 0, R.drawable.cap),
            previewImage = R.drawable.cap_icon
        ),
        ShopCatalogItem(
            category = ShopCategories.HATS,
            item = ShopItem("Бандана", 0, R.drawable.bandana),
            previewImage = R.drawable.bandana_icon
        ),


        ShopCatalogItem(
            category = ShopCategories.ACCESSORIES,
            item = ShopItem("Цветочное ожерелье", 0, R.drawable.floral_neck),
            previewImage = R.drawable.floral_neck_icon
        ),
        ShopCatalogItem(
            category = ShopCategories.ACCESSORIES,
            item = ShopItem("Галстук", 0, R.drawable.tie),
            previewImage = R.drawable.tie_icon
        ),
        ShopCatalogItem(
            category = ShopCategories.ACCESSORIES,
            item = ShopItem("Цепь", 0, R.drawable.chain),
            previewImage = R.drawable.chain_icon
        ),


        ShopCatalogItem(
            category = ShopCategories.SHOES,
            item = ShopItem("Шлепанцы", 0, R.drawable.flipflops),
            previewImage = R.drawable.flipflops_icon
        ),
        ShopCatalogItem(
            category = ShopCategories.SHOES,
            item = ShopItem("Туфли", 0, R.drawable.loafers),
            previewImage = R.drawable.loafers_icon
        ),
        ShopCatalogItem(
            category = ShopCategories.SHOES,
            item = ShopItem("Туфли2", 0, R.drawable.shoes),
            previewImage = R.drawable.shoes_icon
        ),
        ShopCatalogItem(
            category = ShopCategories.SHOES,
            item = ShopItem("Кроссовки", 0, R.drawable.sneakers),
            previewImage = R.drawable.sneakers_icon
        ),


        ShopCatalogItem(
            category = ShopCategories.OTHER,
            item = ShopItem("Кокос", 0, R.drawable.coconut),
            previewImage = R.drawable.coconut_icon
        ),
        ShopCatalogItem(
            category = ShopCategories.OTHER,
            item = ShopItem("Очки", 1, R.drawable.glasses),
            previewImage = R.drawable.glasses_icon
        ),
        ShopCatalogItem(
            category = ShopCategories.OTHER,
            item = ShopItem("Кофе", 0, R.drawable.coffee),
            previewImage = R.drawable.coffee_icon
        )
    )

    val baseItems = listOf(
        ShopCategories.FACE to "Грусть",
        ShopCategories.BACKGROUND to "Помойка"
    )

    fun findItem(category: String, itemName: String): ShopItem? {
        return items.find {
            it.category == category && it.item.name == itemName
        }?.item
    }
}