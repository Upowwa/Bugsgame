package com.example.bugsgame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bugsgame.ui.theme.Green
import com.example.bugsgame.ui.theme.ShopBack
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.grid.items

@Composable
fun ShopScreen(playerId: Int) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val shopRepository = remember { ShopRepository(database) }
    val coroutineScope = rememberCoroutineScope()

    val player by database.playerDao()
        .observePlayer(playerId)
        .collectAsState(initial = null)

    var tab by remember { mutableStateOf(ShopTab.WARDROBE) }
    var category by remember { mutableStateOf<String?>(null) }

    val savedItems by shopRepository
        .observeItems(playerId)
        .collectAsState(initial = emptyList())

    var pendingPurchase by remember { mutableStateOf<Pair<String, ShopItem>?>(null) }
    var showInsufficient by remember { mutableStateOf(false) }

    val categories = getShopCategories()

    val currentCategoryKey = when (tab) {
        ShopTab.INTERIOR -> ShopCategories.BACKGROUND
        ShopTab.WARDROBE -> category ?: ""
    }

    LaunchedEffect(playerId) { shopRepository.initializeShop(playerId) }

    fun isSelected(item: ShopItem): Boolean {
        return savedItems.any {
            it.category == currentCategoryKey && it.itemName == item.name && it.isEquipped
        }
    }

    val orderedItems = ShopCatalog.items
        .filter { it.category == currentCategoryKey }
        .map { catalogItem ->
            val savedItem = savedItems.find {
                it.category == currentCategoryKey && it.itemName == catalogItem.item.name
            }
            catalogItem.copy(
                item = catalogItem.item.copy(owned = savedItem?.isOwned == true)
            )
        }
        .sortedBy { catalogItem ->
            val item = catalogItem.item

            when {
                isSelected(item) -> 0
                item.owned -> 1
                else -> 2
            }
        }

    val previewCategoryOrder = listOf(
        ShopCategories.SHOES,
        ShopCategories.BOTTOM,
        ShopCategories.TOP,
        ShopCategories.ACCESSORIES,
        ShopCategories.HATS
    )

    val equippedPreviewItems = previewCategoryOrder.flatMap { categoryKey ->
        savedItems
            .filter { it.isEquipped && it.category == categoryKey }
            .mapNotNull { savedItem ->
                ShopCatalog.items.find {
                    it.category == savedItem.category && it.item.name == savedItem.itemName
                }
            }
    }

    fun requestPurchase(item: ShopItem) {
        val playerCoins = player?.coins ?: 0
        if (playerCoins >= item.price) {
            pendingPurchase = currentCategoryKey to item
        } else {
            showInsufficient = true
        }
    }

    fun handleItemClick(item: ShopItem) {
        if (!item.owned) {
            requestPurchase(item)
            return
        }
        val selected = isSelected(item)

        coroutineScope.launch {
            if (selected && currentCategoryKey != ShopCategories.FACE &&
                currentCategoryKey != ShopCategories.BACKGROUND
            ) {
                shopRepository.unequip(playerId, currentCategoryKey)
            } else {
                shopRepository.equip(playerId, currentCategoryKey, item.name)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.47f)
        ) {
            Image(
                painter = painterResource(
                    savedItems.find {
                        it.category == ShopCategories.BACKGROUND && it.isEquipped
                    }?.itemName?.let { name ->
                        ShopCatalog.findItem(ShopCategories.BACKGROUND, name)?.image
                    } ?: R.drawable.shop_yard
                ),
                contentDescription = "Фон магазина",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f))
                        .padding(horizontal = 15.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.coins),
                        contentDescription = "Монеты",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(7.dp))

                    Text(
                        text = "${player?.coins ?: 0}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Green
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Image(
                        painter = painterResource(R.drawable.shop_ladybug),
                        contentDescription = "Жук для примерки",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )

                    equippedPreviewItems.forEach { catalogItem ->
                        Image(
                            painter = painterResource(catalogItem.item.image),
                            contentDescription = catalogItem.item.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Image(
                        painter = painterResource(
                            savedItems.find {
                                it.category == ShopCategories.FACE && it.isEquipped
                            }?.itemName?.let { name ->
                                ShopCatalog.findItem(ShopCategories.FACE, name)?.image
                            } ?: R.drawable.face_sad
                        ),
                        contentDescription = "Выбранное лицо",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )

                    savedItems
                        .filter {
                            it.category == ShopCategories.OTHER && it.isEquipped
                        }
                        .forEach { equipped ->
                            val catalogItem = ShopCatalog.items.find {
                                it.category == equipped.category && it.item.name == equipped.itemName
                            }

                            catalogItem?.let {
                                Image(
                                    painter = painterResource(it.item.image),
                                    contentDescription = it.item.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                }
            }
        }
        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .clip(CircleShape)
                .background(ShopBack)
                .border(1.dp, ShopBack, CircleShape)
                .padding(1.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ShopTabButton(
                label = stringResource(R.string.shop_wardrobe),
                selected = tab == ShopTab.WARDROBE,
                modifier = Modifier.weight(1f)
            ) {
                tab = ShopTab.WARDROBE
                category = null
            }

            ShopTabButton(
                label = stringResource(R.string.shop_interior),
                selected = tab == ShopTab.INTERIOR,
                modifier = Modifier.weight(1f)
            ) {
                tab = ShopTab.INTERIOR
                category = null
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(0.53f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                horizontal = 12.dp,
                vertical = 12.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (tab == ShopTab.WARDROBE && category == null) {
                items(categories) { (categoryKey, categoryName) ->
                    ShopCategoryCard(
                        name = categoryName,
                        image = when (categoryKey) {
                            ShopCategories.FACE -> R.drawable.face_smile_icon
                            ShopCategories.TOP -> R.drawable.shirt_icon
                            ShopCategories.BOTTOM -> R.drawable.skirt_icon
                            ShopCategories.HATS -> R.drawable.bandana_icon
                            ShopCategories.ACCESSORIES -> R.drawable.floral_neck_icon
                            ShopCategories.SHOES -> R.drawable.sneakers_icon
                            ShopCategories.OTHER -> R.drawable.coffee_icon
                            else -> R.drawable.coins
                        },
                        onClick = { category = categoryKey }
                    )
                }
            } else {
                items(orderedItems) { catalogItem ->
                    val item = catalogItem.item
                    ShopProductCard(
                        catalogItem = catalogItem,
                        selected = isSelected(item),
                        isInterior = tab == ShopTab.INTERIOR,
                        isFaceCategory = currentCategoryKey == ShopCategories.FACE,
                        onClick = { handleItemClick(item) }
                    )
                }
            }
        }
    }

    pendingPurchase?.let { (categoryKey, item) ->
        ShopMessageDialog(
            title = "Купить?",
            message = "",
            coinAmount = item.price,
            primaryAction = "Купить",
            secondaryAction = "Отмена",
            onPrimary = {
                coroutineScope.launch {
                    val success = shopRepository.purchase(
                        playerId,
                        categoryKey,
                        item.name,
                        item.price
                    )
                    pendingPurchase = null

                    if (!success) {
                        showInsufficient = true
                    }
                }
            },
            onSecondary = { pendingPurchase = null },
            onDismiss = { pendingPurchase = null }
        )
    }

    if (showInsufficient) {
        ShopMessageDialog(
            title = "Недостаточно монет",
            message = "Сыграй в игру, чтобы\nзаработать больше монет",
            primaryAction = "Понятно",
            onPrimary = { showInsufficient = false },
            onDismiss = { showInsufficient = false }
        )
    }
}

@Composable
private fun getShopCategories(): List<Pair<String, String>> {
    return ShopCatalog.wardrobeCategories.map { (key, titleRes) ->
        key to stringResource(titleRes)
    }
}