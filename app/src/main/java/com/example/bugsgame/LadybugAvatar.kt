package com.example.bugsgame

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun LadybugAvatar(
    items: List<ShopItemEntity>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val background = items.find {
            it.category == ShopCategories.BACKGROUND && it.isEquipped
        }?.let { equipped ->
            ShopCatalog.findItem(
                ShopCategories.BACKGROUND,
                equipped.itemName
            )?.image
        } ?: R.drawable.shop_yard

        Image(
            painter = painterResource(background),
            contentDescription = "Фон аватара",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val topOffset = maxHeight * 0.1f
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = topOffset)
            ) {
                Image(
                    painter = painterResource(R.drawable.shop_ladybug),
                    contentDescription = "Жук",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                val categoryOrder = listOf(
                    ShopCategories.SHOES,
                    ShopCategories.BOTTOM,
                    ShopCategories.TOP,
                    ShopCategories.ACCESSORIES,
                    ShopCategories.HATS
                )

                categoryOrder.forEach { category ->
                    items
                        .filter { it.category == category && it.isEquipped }
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

                val face = items.find {
                    it.category == ShopCategories.FACE && it.isEquipped
                }?.let { equipped ->
                    ShopCatalog.findItem(
                        ShopCategories.FACE,
                        equipped.itemName
                    )?.image
                } ?: R.drawable.face_sad

                Image(
                    painter = painterResource(face),
                    contentDescription = "Выражение лица",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                items
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
}
