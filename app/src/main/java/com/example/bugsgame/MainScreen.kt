package com.example.bugsgame

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bugsgame.ui.theme.Green

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        "Регистрация",
        "Правила",
        "Авторы",
        "Настройки"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEachIndexed { index, title ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            selectedTab = index
                        }
                        .padding(horizontal = 2.dp)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = if (selectedTab == index) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        },
                        color = if (selectedTab == index) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Green
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(2.dp)
                            .background(
                                if (selectedTab == index) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    Color.Transparent
                                }
                            )
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> RegistrationScreen()
            1 -> RulesScreen()
            2 -> AuthorsScreen()
            3 -> SettingsScreen()
        }
    }
}