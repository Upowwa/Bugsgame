package com.example.bugsgame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.bugsgame.ui.theme.Green
import com.example.bugsgame.ui.theme.ShopBack

@Composable
fun ShopTabButton(
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
            color = if (selected) Color.White else Green,
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
fun ShopProductCard(
    catalogItem: ShopCatalogItem,
    selected: Boolean,
    isInterior: Boolean,
    isFaceCategory: Boolean,
    onClick: () -> Unit
) {
    val item = catalogItem.item
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
            painter = painterResource(catalogItem.previewImage),
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
fun ShopCategoryCard(
    name: String,
    image: Int,
    onClick: () -> Unit
){
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
            painter = painterResource(image),
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
fun ShopMessageDialog(
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
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
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