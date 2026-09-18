package com.example.bugsgame

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var editTextFullName: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var spinnerCourse: Spinner
    private lateinit var seekBarDifficulty: SeekBar
    private lateinit var textViewDifficulty: TextView
    private lateinit var calendarViewBirthDate: CalendarView

    private lateinit var spinnerBirthYear: Spinner
    private lateinit var buttonRegister: Button
    private lateinit var imageViewZodiac: ImageView
    private lateinit var textViewResult: TextView

    private var selectedBirthDate = Calendar.getInstance()

    private var player: Player? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        editTextFullName = findViewById(R.id.editTextFullName)
        radioGroupGender = findViewById(R.id.radioGroupGender)
        spinnerCourse = findViewById(R.id.spinnerCourse)
        seekBarDifficulty = findViewById(R.id.seekBarDifficulty)
        textViewDifficulty = findViewById(R.id.textViewDifficulty)
        calendarViewBirthDate = findViewById(R.id.calendarViewBirthDate)
        spinnerBirthYear = findViewById(R.id.spinnerBirthYear)
        buttonRegister = findViewById(R.id.buttonRegister)
        imageViewZodiac = findViewById(R.id.imageViewZodiac)
        textViewResult = findViewById(R.id.textViewResult)

        setupCourseSpinner()
        setupBirthYearSpinner()
        setupDifficulty()
        setupCalendar()
        setupRegisterButton()
    }

    private fun setupCourseSpinner() {
        val courses = arrayOf(
            "1 курс",
            "2 курс",
            "3 курс",
            "4 курс"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            courses
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerCourse.adapter = adapter
    }

    private fun setupBirthYearSpinner() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val years = (1950..currentYear).toList().reversed()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            years
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerBirthYear.adapter = adapter

        spinnerBirthYear.setSelection(
            years.indexOf(selectedBirthDate.get(Calendar.YEAR))
        )

        spinnerBirthYear.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedYear = years[position]

                    selectedBirthDate.set(
                        Calendar.YEAR,
                        selectedYear
                    )

                    calendarViewBirthDate.date =
                        selectedBirthDate.timeInMillis
                }

                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {
                }
            }
    }

    private fun setupDifficulty() {
        seekBarDifficulty.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    val difficulty = progress + 1
                    textViewDifficulty.text = getString(R.string.difficulty, difficulty)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            }
        )
    }

    private fun setupCalendar() {
        calendarViewBirthDate.setOnDateChangeListener {
                _, year, month, dayOfMonth ->

            selectedBirthDate.set(
                year,
                month,
                dayOfMonth
            )
        }
    }

    private fun setupRegisterButton() {
        buttonRegister.setOnClickListener {
            registerPlayer()
        }
    }

    private fun registerPlayer() {
        val fullName = editTextFullName.text.toString().trim()

        if (fullName.isEmpty()) {
            editTextFullName.error = "Введите ФИО"
            return
        }

        val gender = when (radioGroupGender.checkedRadioButtonId) {
            R.id.radioMale -> "Мужской"
            R.id.radioFemale -> "Женский"
            else -> "Не указан"
        }

        val course = spinnerCourse.selectedItem.toString()

        val difficulty = seekBarDifficulty.progress + 1

        val birthDate = SimpleDateFormat(
            "dd.MM.yyyy",
            Locale.getDefault()
        ).format(selectedBirthDate.time)

        val zodiac = getZodiacSign(
            selectedBirthDate.get(Calendar.DAY_OF_MONTH),
            selectedBirthDate.get(Calendar.MONTH) + 1
        )

        player = Player(
            fullName = fullName,
            gender = gender,
            course = course,
            difficulty = difficulty,
            birthDate = birthDate,
            zodiac = zodiac
        )

        showPlayerData()
        showZodiacImage(zodiac)
    }

    private fun showPlayerData() {
        val currentPlayer = player ?: return

        textViewResult.text = """
            Данные игрока:
            
            ФИО: ${currentPlayer.fullName}
            Пол: ${currentPlayer.gender}
            Курс: ${currentPlayer.course}
            Уровень сложности: ${currentPlayer.difficulty}
            Дата рождения: ${currentPlayer.birthDate}
            Знак зодиака: ${currentPlayer.zodiac}
        """.trimIndent()
    }

    private fun getZodiacSign(day: Int, month: Int): String {
        return when {
            (month == 12 && day >= 22) || (month == 1 && day <= 19) ->
                "Козерог"

            (month == 1 && day >= 20) || (month == 2 && day <= 18) ->
                "Водолей"

            (month == 2 && day >= 19) || (month == 3 && day <= 20) ->
                "Рыбы"

            (month == 3 && day >= 21) || (month == 4 && day <= 19) ->
                "Овен"

            (month == 4 && day >= 20) || (month == 5 && day <= 20) ->
                "Телец"

            (month == 5 && day >= 21) || (month == 6 && day <= 20) ->
                "Близнецы"

            (month == 6 && day >= 21) || (month == 7 && day <= 22) ->
                "Рак"

            (month == 7 && day >= 23) || (month == 8 && day <= 22) ->
                "Лев"

            (month == 8 && day >= 23) || (month == 9 && day <= 22) ->
                "Дева"

            (month == 9 && day >= 23) || (month == 10 && day <= 22) ->
                "Весы"

            (month == 10 && day >= 23) || (month == 11 && day <= 21) ->
                "Скорпион"

            else ->
                "Стрелец"
        }
    }

    private fun showZodiacImage(zodiac: String) {
        imageViewZodiac.visibility = ImageView.VISIBLE

        when (zodiac) {
            "Козерог" -> imageViewZodiac.setImageResource(R.drawable.zodiac_capricorn)
            "Водолей" -> imageViewZodiac.setImageResource(R.drawable.zodiac_aquarius)
            "Рыбы" -> imageViewZodiac.setImageResource(R.drawable.zodiac_pisces)
            "Овен" -> imageViewZodiac.setImageResource(R.drawable.zodiac_aries)
            "Телец" -> imageViewZodiac.setImageResource(R.drawable.zodiac_taurus)
            "Близнецы" -> imageViewZodiac.setImageResource(R.drawable.zodiac_gemini)
            "Рак" -> imageViewZodiac.setImageResource(R.drawable.zodiac_cancer)
            "Лев" -> imageViewZodiac.setImageResource(R.drawable.zodiac_leo)
            "Дева" -> imageViewZodiac.setImageResource(R.drawable.zodiac_virgo)
            "Весы" -> imageViewZodiac.setImageResource(R.drawable.zodiac_libra)
            "Скорпион" -> imageViewZodiac.setImageResource(R.drawable.zodiac_scorpio)
            "Стрелец" -> imageViewZodiac.setImageResource(R.drawable.zodiac_sagittarius)
        }
    }
}