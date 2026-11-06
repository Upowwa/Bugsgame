package com.example.bugsgame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.bugsgame.ui.theme.Green
import com.example.bugsgame.ui.theme.ShopBack
import androidx.compose.ui.res.stringResource

private enum class ShopTab {
    INTERIOR,
    WARDROBE
}

private data class ShopItem(
    val name: String,
    val price: Int,
    val image: Int,
    val owned: Boolean = false
)

private data class EquippedShopItem(
    val category: String,
    val name: String
)

@Composable
fun ShopScreen(playerId: Int) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }

    val player by database.playerDao()
        .observePlayer(playerId)
        .collectAsState(initial = null)

    var tab by remember { mutableStateOf(ShopTab.WARDROBE) }
    var category by remember { mutableStateOf<String?>(null) }
    var selectedFace by remember { mutableIntStateOf(R.drawable.face_sad) }
    var selectedBackground by remember { mutableIntStateOf(R.drawable.shop_yard) }
    var equippedItems by remember { mutableStateOf(setOf<EquippedShopItem>()) }
    var dialogItem by remember { mutableStateOf<ShopItem?>(null) }
    var showInsufficient by remember { mutableStateOf(false) }

    val faceCategory = stringResource(R.string.shop_category_face)
    val topCategory = stringResource(R.string.shop_category_top)
    val bottomCategory = stringResource(R.string.shop_category_bottom)
    val hatsCategory = stringResource(R.string.shop_category_hats)
    val accessoriesCategory = stringResource(R.string.shop_category_accessories)
    val shoesCategory = stringResource(R.string.shop_category_shoes)

    val categories = listOf(
        faceCategory,
        topCategory,
        bottomCategory,
        hatsCategory,
        accessoriesCategory,
        shoesCategory
    )

    val shopItems = when (tab) {
        ShopTab.INTERIOR -> listOf(
            ShopItem("Тропический пляж", 50, R.drawable.shop_beach),
            ShopItem("Реперский зал", 540, image = R.drawable.shop_hall),
            ShopItem("Уютный офис", 420, R.drawable.shop_office, owned = true),
            ShopItem("Помойка", 0, R.drawable.shop_yard, owned = true)
        )

        ShopTab.WARDROBE -> when (category) {
            faceCategory -> listOf(
                ShopItem("Улыбка", 120, R.drawable.face_smile, owned = true),
                ShopItem("Радость", 120, R.drawable.face_grin, owned = true),
                ShopItem("Спокойствие", 90, R.drawable.face_neutral, owned = true),
                ShopItem("Грусть", 0, R.drawable.face_sad, owned = true)
            )

            accessoriesCategory -> listOf(
                ShopItem("Галстук", 0, R.drawable.shop_tie_icon, owned = true)
            )
            else -> emptyList()
        }
    }

    fun isSelected(item: ShopItem): Boolean {
        return if (tab == ShopTab.INTERIOR) {
            selectedBackground == item.image
        } else if (category == faceCategory) {
            item.image == selectedFace
        } else {
            category?.let { selectedCategory ->
                EquippedShopItem(selectedCategory, item.name) in equippedItems
            } ?: false
        }
    }

    val orderedItems = shopItems.sortedBy { item ->
        when {
            isSelected(item) -> 0
            item.owned -> 1
            else -> 2
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
                painter = painterResource(selectedBackground),
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

                    if (EquippedShopItem(accessoriesCategory, "Галстук") in equippedItems) {
                        Image(
                            painter = painterResource(R.drawable.shop_tie),
                            contentDescription = "Галстук на жуке",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Image(
                        painter = painterResource(selectedFace),
                        contentDescription = "Выбранное лицо",
                        modifier = Modifier
                            .size(width = 58.dp, height = 58.dp)
                            .align(Alignment.Center)
                            .offset(y = (-18).dp),
                        contentScale = ContentScale.Fit
                    )
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
                items(categories) { categoryName ->
                    ShopCategoryCard(
                        name = categoryName,
                        onClick = { category = categoryName }
                    )
                }
            } else {
                items(orderedItems) { item ->
                    ShopProductCard(
                        item = item,
                        selected = isSelected(item),
                        isInterior = tab == ShopTab.INTERIOR,
                        isFaceCategory = category == faceCategory
                    ) {
                        when (tab) {
                            ShopTab.INTERIOR -> {
                                if (item.owned) {
                                    selectedBackground = item.image
                                } else {
                                    val playerCoins = player?.coins ?: 0

                                    if (playerCoins >= item.price) {
                                        dialogItem = item
                                    } else {
                                        showInsufficient = true
                                    }
                                }
                            }

                            ShopTab.WARDROBE -> {
                                when {
                                    item.owned && category == faceCategory -> { selectedFace = item.image }

                                    item.owned && category != null -> {
                                        category?.let { selectedCategory ->
                                            val worn = EquippedShopItem(selectedCategory, item.name)

                                            equippedItems = if (worn in equippedItems) {
                                                equippedItems - worn
                                            } else {
                                                equippedItems + worn
                                            }
                                        }
                                    }

                                    else -> {
                                        val playerCoins = player?.coins ?: 0

                                        if (playerCoins >= item.price) {
                                            dialogItem = item
                                        } else {
                                            showInsufficient = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    dialogItem?.let { item ->
        ShopMessageDialog(
            title = "Купить?",
            message = "",
            coinAmount = item.price,
            primaryAction = "Купить",
            secondaryAction = "Отмена",
            onPrimary = { dialogItem = null },
            onSecondary = { dialogItem = null },
            onDismiss = { dialogItem = null }
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
private fun ShopTabButton(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    ShopBack
                }
            )
            .clickable(onClick = onClick)
            .padding(vertical = 11.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) {
                Color.White
            } else {
                Green
            },
            fontSize = 20.sp,
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Medium
            }
        )
    }
}

@Composable
private fun ShopProductCard(
    item: ShopItem,
    selected: Boolean,
    isInterior: Boolean,
    isFaceCategory: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(172.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(ShopBack)
            .clickable(onClick = onClick)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(item.image),
            contentDescription = item.name,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(14.dp)),
            contentScale = if (isInterior) {
                ContentScale.Crop
            } else {
                ContentScale.Fit
            }
        )
        Spacer(Modifier.height(15.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(
                    if (selected && (isFaceCategory || isInterior)) {
                        Color.Transparent
                    } else if (selected) {
                        Color.White
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
                .then(
                    if (selected && !isFaceCategory && !isInterior) {
                        Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                selected && (isFaceCategory || isInterior) -> {
                    Image(
                        painter = painterResource(R.drawable.check),
                        contentDescription = "Выбрано",
                        modifier = Modifier.size(22.dp)
                    )
                }

                selected -> {
                    Text(
                        text = stringResource(R.string.shop_remove),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                item.owned -> {
                    Text(
                        text = stringResource(R.string.shop_select),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                else -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = item.price.toString(),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Image(
                            painter = painterResource(R.drawable.coins),
                            contentDescription = "Монеты",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShopCategoryCard(
    name: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(172.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(ShopBack)
            .clickable(onClick = onClick)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.coins),
            contentDescription = name,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.height(6.dp))

        Text(
            text = name,
            color = Green,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ShopMessageDialog(
    title: String,
    message: String,
    coinAmount: Int? = null,
    primaryAction: String,
    secondaryAction: String? = null,
    onPrimary: () -> Unit,
    onSecondary: () -> Unit = {},
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            modifier = Modifier.border(
                1.5.dp,
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(18.dp)
            )
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 30.dp,
                    vertical = 25.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(20.dp))

                if (coinAmount != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Стоимость: $coinAmount",
                            color = Green,
                            fontSize = 17.sp
                        )
                        Image(
                            painter = painterResource(R.drawable.coins),
                            contentDescription = "Монеты",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    Text(
                        text = message,
                        color = Green,
                        fontSize = 17.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(25.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable(onClick = onPrimary)
                            .padding(vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = primaryAction,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp
                        )
                    }

                    if (secondaryAction != null) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                .clickable(onClick = onSecondary)
                                .padding(vertical = 5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = secondaryAction,
                                color = Green,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
