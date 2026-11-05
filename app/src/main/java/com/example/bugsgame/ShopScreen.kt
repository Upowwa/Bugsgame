package com.example.bugsgame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ShopPurple = Color(0xFF6650A4)
private val ShopPurpleDark = Color(0xFF6650A4)
private val CardGray = Color(0xFFF1F1F1)
private enum class ShopTab { INTERIOR, WARDROBE }
private data class ShopItem(val name: String, val price: Int, val image: Int? = null, val color: Color = Color.White, val owned: Boolean = false)
private data class EquippedShopItem(val category: String, val name: String)

@Composable
fun ShopScreen(playerId: Int) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val player by database.playerDao().observePlayer(playerId).collectAsState(initial = null)
    var tab by remember { mutableStateOf(ShopTab.INTERIOR) }
    var category by remember { mutableStateOf("Лица") }
    var selectedFace by remember { mutableIntStateOf(R.drawable.face_sad) }
    var selectedBackground by remember { mutableIntStateOf(R.drawable.shop_yard) }
    var equippedItems by remember { mutableStateOf(setOf<EquippedShopItem>()) }
    var dialogItem by remember { mutableStateOf<ShopItem?>(null) }
    var showInsufficient by remember { mutableStateOf(false) }
    val categories = listOf("Лица", "Аксессуары", "Верх", "Низ", "Обувь")

    val items = when (tab) {
        ShopTab.INTERIOR -> listOf(
            ShopItem("Тропический пляж", 540, R.drawable.shop_beach),
            ShopItem("Реперский зал", 540, R.drawable.shop_hall),
            ShopItem("Уютный офис", 420, R.drawable.shop_office, owned = true),
            ShopItem("Помойка", 0, R.drawable.shop_yard, owned = true)
        )
        else -> when (category) {
            "Лица" -> listOf(
                ShopItem("Улыбка", 120, R.drawable.face_smile, owned = true),
                ShopItem("Радость", 120, R.drawable.face_grin, owned = true),
                ShopItem("Спокойствие", 90, R.drawable.face_neutral, owned = true),
                ShopItem("Грусть", 0, R.drawable.face_sad, owned = true)
            )
            "Аксессуары" -> listOf(ShopItem("Галстук", 0, R.drawable.shop_tie_icon, owned = true), ShopItem("Солнечные очки", 180, color = Color(0xFF83D7CD)), ShopItem("Шляпа", 220, color = Color(0xFFFFD166)), ShopItem("Бантик", 140, color = Color(0xFFF59ABB)))
            "Верх" -> listOf(ShopItem("Футболка", 0, color = Color(0xFFB7A1E8), owned = true), ShopItem("Мятный свитер", 240, color = Color(0xFF77D7CB)), ShopItem("Белая рубашка", 540, color = Color(0xFFE7E7E7)), ShopItem("Оранжевый топ", 320, color = Color(0xFFFFAA68)))
            "Низ" -> listOf(ShopItem("Шорты", 0, color = Color(0xFFB9B7D5), owned = true), ShopItem("Юбка", 260, color = Color(0xFFF2A8C9)), ShopItem("Джинсы", 360, color = Color(0xFF7FA9D8)))
            else -> listOf(ShopItem("Кеды", 0, color = Color(0xFFEEE9E0), owned = true), ShopItem("Сапожки", 280, color = Color(0xFFDB9B5B)), ShopItem("Тапочки", 160, color = Color(0xFF9CD8CC)))
        }
    }
    fun isSelected(item: ShopItem): Boolean = if (tab == ShopTab.INTERIOR) {
        selectedBackground == item.image
    } else if (category == "Лица") item.image == selectedFace
    else EquippedShopItem(category, item.name) in equippedItems
    val orderedItems = items.sortedBy { item ->
        when {
            isSelected(item) -> 0
            item.owned -> 1
            else -> 2
        }
    }

    Column(Modifier.fillMaxSize().background(Color.White)) {
        Box(Modifier.fillMaxWidth().weight(0.47f)) {
            Image(painterResource(selectedBackground), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Row(Modifier.align(Alignment.TopStart).padding(14.dp).clip(CircleShape).background(Color.White.copy(alpha = .9f)).padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(R.drawable.coins), "Монеты", Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("${player?.coins ?: 0}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Image(painterResource(R.drawable.shop_ladybug), "Жук для примерки", Modifier.fillMaxHeight(.84f).align(Alignment.BottomCenter), contentScale = ContentScale.Fit)
            if (EquippedShopItem("Аксессуары", "Галстук") in equippedItems) {
                Image(painterResource(R.drawable.shop_tie), "Галстук на жуке", Modifier.fillMaxHeight(.84f).align(Alignment.BottomCenter), contentScale = ContentScale.Fit)
            }
            Image(painterResource(selectedFace), "Выбранное лицо", Modifier.fillMaxWidth(.19f).height(58.dp).align(Alignment.Center).offset(y = (-18).dp), contentScale = ContentScale.Fit)
        }

        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ShopTabButton("Интерьер", tab == ShopTab.INTERIOR, Modifier.weight(1f)) { tab = ShopTab.INTERIOR }
            ShopTabButton("Гардероб", tab == ShopTab.WARDROBE, Modifier.weight(1f)) { tab = ShopTab.WARDROBE }
        }
        if (tab == ShopTab.WARDROBE) {
            Row(Modifier.fillMaxWidth().height(42.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                categories.forEach { label ->
                    val active = category == label
                    Text(label, Modifier.clip(CircleShape).then(if (active) Modifier.background(ShopPurple) else Modifier.border(1.dp, ShopPurple, CircleShape)).clickable { category = label }.padding(horizontal = 8.dp, vertical = 8.dp), color = if (active) Color.White else ShopPurple, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        } else {
            Spacer(Modifier.height(42.dp))
        }
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(.53f).fillMaxWidth(), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(orderedItems) { item ->
                ShopProductCard(item, isSelected(item), tab == ShopTab.INTERIOR, if (category == "Лица") "✓ Выбрано" else "Снять") {
                    if (tab == ShopTab.INTERIOR && item.owned) selectedBackground = item.image!!
                    else if (tab == ShopTab.WARDROBE && item.owned) {
                        if (category == "Лица") {
                            item.image?.let { selectedFace = it }
                        } else {
                            val worn = EquippedShopItem(category, item.name)
                            equippedItems = if (worn in equippedItems) {
                                equippedItems - worn
                            } else {
                                val otherCategories = if (category == "Аксессуары") equippedItems else equippedItems.filterNot { it.category == category }.toSet()
                                otherCategories + worn
                            }
                        }
                    } else dialogItem = item
                }
            }
        }
    }
    dialogItem?.let { item ->
        ShopMessageDialog(
            title = "Купить «${item.name}»?",
            message = "",
            coinAmount = item.price,
            primaryAction = "Купить",
            secondaryAction = "Отмена",
            onPrimary = { dialogItem = null; showInsufficient = true },
            onSecondary = { dialogItem = null },
            onDismiss = { dialogItem = null }
        )
    }
    if (showInsufficient) ShopMessageDialog(
        title = "Недостаточно монет",
        message = "Для этой покупки пока не хватает монет.",
        primaryAction = "Понятно",
        onPrimary = { showInsufficient = false },
        onDismiss = { showInsufficient = false }
    )
}

@Composable
private fun ShopTabButton(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(modifier.clip(CircleShape).then(if (selected) Modifier.background(ShopPurple) else Modifier.border(1.dp, ShopPurple, CircleShape)).clickable(onClick = onClick).padding(vertical = 11.dp), contentAlignment = Alignment.Center) {
        Text(label, color = if (selected) Color.White else ShopPurple, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ShopProductCard(item: ShopItem, selected: Boolean, isInterior: Boolean, selectedLabel: String, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().height(172.dp).clip(RoundedCornerShape(22.dp)).background(CardGray).clickable(onClick = onClick).padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (item.image != null) Image(painterResource(item.image), item.name, Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(14.dp)), contentScale = if (isInterior) ContentScale.Crop else ContentScale.Fit)
        else Box(Modifier.fillMaxWidth(.64f).weight(1f).clip(RoundedCornerShape(22.dp)).background(item.color), contentAlignment = Alignment.Center) { Text(if (item.name.contains("очки")) "◉  ◉" else "✦", fontSize = 25.sp, color = Color.DarkGray) }
        Spacer(Modifier.height(6.dp))
        Text(
            text = item.name,
            modifier = Modifier.fillMaxWidth().height(38.dp),
            color = Color(0xFF292431),
            fontSize = 15.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
        Spacer(Modifier.height(6.dp))
        Box(Modifier.fillMaxWidth().clip(CircleShape).background(if (selected) Color.White else ShopPurple).padding(vertical = 7.dp), contentAlignment = Alignment.Center) {
            if (selected) {
                Text(if (isInterior) "✓ Выбрано" else selectedLabel, color = ShopPurple, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            } else if (item.owned) {
                Text("Надеть", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(item.price.toString(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Image(painterResource(R.drawable.coins), "Монеты", Modifier.size(18.dp))
                }
            }
        }
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
        Surface(shape = RoundedCornerShape(18.dp), color = Color.White, modifier = Modifier.border(1.dp, ShopPurple, RoundedCornerShape(18.dp))) {
            Column(Modifier.padding(horizontal = 22.dp, vertical = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, color = ShopPurpleDark, fontSize = 18.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
                Spacer(Modifier.height(10.dp))
                if (coinAmount != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("Стоимость: $coinAmount", color = Color(0xFF4F4A59), fontSize = 15.sp)
                        Image(painterResource(R.drawable.coins), "Монеты", Modifier.size(18.dp))
                    }
                } else {
                    Text(message, color = Color(0xFF4F4A59), fontSize = 15.sp, textAlign = TextAlign.Center)
                }
                Spacer(Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (secondaryAction != null) {
                        Box(Modifier.weight(1f).clip(CircleShape).border(1.dp, ShopPurple, CircleShape).clickable(onClick = onSecondary).padding(vertical = 9.dp), contentAlignment = Alignment.Center) {
                            Text(secondaryAction, color = ShopPurpleDark, fontSize = 14.sp)
                        }
                    }
                    Box(Modifier.weight(1f).clip(CircleShape).background(ShopPurple).clickable(onClick = onPrimary).padding(vertical = 9.dp), contentAlignment = Alignment.Center) {
                        Text(primaryAction, color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

