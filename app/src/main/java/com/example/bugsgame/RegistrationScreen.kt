package com.example.bugsgame

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.example.bugsgame.ui.theme.Green

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen() {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("1 курс") }
    var difficulty by remember { mutableIntStateOf(1) }
    var birthDate by remember { mutableStateOf(Calendar.getInstance()) }
    var showCourseMenu by remember { mutableStateOf(false) }
    var player by remember { mutableStateOf<Player?>(null) }

    val courses = listOf(
        "1 курс",
        "2 курс",
        "3 курс",
        "4 курс"
    )

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.registration_title),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Green
        )

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.full_name)) },
            textStyle = TextStyle(
                fontSize = 17.sp,
                color = Green
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = stringResource(R.string.gender),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Green
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = gender == "Мужской",
                    onClick = {
                        gender = "Мужской"
                        focusManager.clearFocus()
                    }
                )

                Text(
                    text = stringResource(R.string.male),
                    fontSize = 17.sp,
                    color = Green
                )
            }

            Spacer(modifier = Modifier.padding(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = gender == "Женский",
                    onClick = {
                        gender = "Женский"
                        focusManager.clearFocus()
                    }
                )

                Text(
                    text = stringResource(R.string.female),
                    fontSize = 17.sp,
                    color = Green
                )
            }
        }

        Spacer(modifier = Modifier.height(17.dp))

        Text(
            text = stringResource(R.string.course),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Green
        )

        Spacer(modifier = Modifier.height(15.dp))

        ExposedDropdownMenuBox(
            expanded = showCourseMenu,
            onExpandedChange = {
                focusManager.clearFocus()
                showCourseMenu = !showCourseMenu },
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedTextField(
                value = course,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    ),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCourseMenu)
                },
                textStyle = TextStyle(
                    fontSize = 17.sp,
                    color = Green
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
            )

            ExposedDropdownMenu(
                expanded = showCourseMenu,
                onDismissRequest = {
                    showCourseMenu = false
                }
            ) {
                courses.forEach { selectedCourse ->

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = selectedCourse,
                                fontSize = 17.sp,
                                color = Green
                            ) },
                        onClick = {
                            course = selectedCourse
                            showCourseMenu = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = stringResource(R.string.difficulty, difficulty),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Green
        )
        Spacer(modifier = Modifier.height(10.dp))
        Slider(
            value = difficulty.toFloat(),
            onValueChange = { difficulty = it.toInt() },
            valueRange = 1f..5f,
            steps = 3,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = stringResource(R.string.birth_date),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Green
        )

        Spacer(modifier = Modifier.height(7.dp))

        val dateFormat = SimpleDateFormat("dd.MM.yyyy", LocalLocale.current.platformLocale)

        OutlinedButton(
            onClick = {
                focusManager.clearFocus()
                val current = birthDate

                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        birthDate = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }
                    },
                    current.get(Calendar.YEAR),
                    current.get(Calendar.MONTH),
                    current.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Green
            ),
            border = BorderStroke(
                1.5.dp,
                MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = dateFormat.format(birthDate.time),
                fontSize = 17.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (fullName.isBlank()) {
                    return@Button
                }

                val zodiac = getZodiacSign(
                    birthDate.get(Calendar.DAY_OF_MONTH),
                    birthDate.get(Calendar.MONTH) + 1
                )

                player = Player(
                    fullName = fullName.trim(),
                    gender = gender.ifEmpty {
                        "Не указан"
                    },
                    course = course,
                    difficulty = difficulty,
                    birthDate = dateFormat.format(birthDate.time),
                    zodiac = zodiac
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.register),
                fontSize = 25.sp
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        player?.let { currentPlayer ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = stringResource(
                        R.string.player_result,
                        currentPlayer.fullName,
                        currentPlayer.gender,
                        currentPlayer.course,
                        currentPlayer.difficulty,
                        currentPlayer.birthDate,
                        currentPlayer.zodiac
                    ),
                    fontSize = 20.sp,
                    color = Green
                )
                Spacer(modifier = Modifier.height(20.dp))

                Image(
                    painter = painterResource(id = getZodiacImage(currentPlayer.zodiac)),
                    contentDescription = stringResource(R.string.zodiac_sign),
                    modifier = Modifier.size(150.dp)
                )
                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }
}

private fun getZodiacSign(day: Int, month: Int): String {
    return when (month) {
        1 -> if (day <= 19) "Козерог" else "Водолей"
        2 -> if (day <= 18) "Водолей" else "Рыбы"
        3 -> if (day <= 20) "Рыбы" else "Овен"
        4 -> if (day <= 19) "Овен" else "Телец"
        5 -> if (day <= 20) "Телец" else "Близнецы"
        6 -> if (day <= 20) "Близнецы" else "Рак"
        7 -> if (day <= 22) "Рак" else "Лев"
        8 -> if (day <= 22) "Лев" else "Дева"
        9 -> if (day <= 22) "Дева" else "Весы"
        10 -> if (day <= 22) "Весы" else "Скорпион"
        11 -> if (day <= 21) "Скорпион" else "Стрелец"
        12 -> if (day <= 21) "Стрелец" else "Козерог"
        else -> "Козерог"
    }
}

private fun getZodiacImage(zodiac: String): Int {
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