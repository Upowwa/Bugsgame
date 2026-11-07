package com.example.bugsgame

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.bugsgame.ui.theme.Green

@Composable
fun PlayerInfoScreen(
    player: Player,
    onChangePlayer: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val shopRepository = remember { ShopRepository(database) }
    val equippedItems by shopRepository.observeItems(player.id).collectAsState(initial = emptyList())
    val profileSectionSpacing = 16.dp
    LaunchedEffect(player.id) {
        shopRepository.initializeShop(player.id)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.infoplay_back2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Вы вошли как ${player.fullName}...",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Green
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileBug(items = equippedItems)
                Spacer(modifier = Modifier.height(profileSectionSpacing))
                Column(
                    modifier = Modifier.width(IntrinsicSize.Max),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("Данные игрока:", fontSize = 16.sp, color = Green)
                    Spacer(modifier = Modifier.height(profileSectionSpacing))
                    Text("ФИО: ${player.fullName}", fontSize = 14.sp, color = Green)
                    Text("Пол: ${player.gender}", fontSize = 14.sp, color = Green)
                    Text("Курс: ${player.course}", fontSize = 14.sp, color = Green)
                    Text("Дата рождения: ${player.birthDate}", fontSize = 14.sp, color = Green)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Start
                    ) {
                        Text("Знак зодиака: ${player.zodiac}", fontSize = 14.sp, color = Green)
                        Spacer(modifier = Modifier.width(4.dp))
                        Image(
                            painter = painterResource(id = getZodiacImageForInfo(player.zodiac)),
                            contentDescription = stringResource(R.string.zodiac_sign),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onChangePlayer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.change_player),
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
private fun ProfileBug(items: List<ShopItemEntity>) {
    Box(modifier = Modifier.size(width = 240.dp, height = 180.dp), contentAlignment = Alignment.Center) {
        val background = items.find {
            it.isEquipped && it.category == ShopCategories.BACKGROUND
        }?.let { ShopCatalog.findItem(it.category, it.itemName)?.image }
            ?: R.drawable.shop_yard
        Image(
            painter = painterResource(background),
            contentDescription = "Фон аватара",
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(R.drawable.shop_ladybug),
            contentDescription = "Жук игрока",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        listOf(ShopCategories.SHOES, ShopCategories.BOTTOM, ShopCategories.TOP,
            ShopCategories.ACCESSORIES, ShopCategories.HATS).forEach { category ->
            items.filter { it.isEquipped && it.category == category }.forEach { item ->
                ShopCatalog.items.find {
                    it.category == item.category && it.item.name == item.itemName
                }?.let { catalogItem ->
                    if (catalogItem.category in setOf(
                            ShopCategories.TOP,
                            ShopCategories.BOTTOM,
                            ShopCategories.SHOES
                        )
                    ) {
                        EquippedClothingLayer(catalogItem, bugHeight = 180.dp)
                    } else {
                        Image(
                            painter = painterResource(catalogItem.item.image),
                            contentDescription = catalogItem.item.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
        val face = items.find { it.isEquipped && it.category == ShopCategories.FACE }
            ?.let { ShopCatalog.findItem(ShopCategories.FACE, it.itemName)?.image }
            ?: R.drawable.sad_icon
        Image(
            painter = painterResource(face),
            contentDescription = "Выбранное лицо жука",
            modifier = Modifier.size(width = 32.dp, height = 32.dp).offset(y = (-28).dp),
            contentScale = ContentScale.Fit
        )
    }
}

private fun getZodiacImageForInfo(zodiac: String): Int {
    return when (zodiac) {
        "Козерог" -> R.drawable.zodiac_capricorn
        "Водолей" -> R.drawable.zodiac_aquarius
        "Рыбы" -> R.drawable.zodiac_pisces
        "Овен" -> R.drawable.zodiac_aries
        "Телец" -> R.drawable.zodiac_taurus
        "Близнецы" -> R.drawable.zodiac_gemini
        "Рак" -> R.drawable.zodiac_cancer
        "Лев" -> R.drawable.zodiac_leo
        "Дева" -> R.drawable.zodiac_virgo
        "Весы" -> R.drawable.zodiac_libra
        "Скорпион" -> R.drawable.zodiac_scorpio
        "Стрелец" -> R.drawable.zodiac_sagittarius
        else -> R.drawable.zodiac_capricorn
    }
}
