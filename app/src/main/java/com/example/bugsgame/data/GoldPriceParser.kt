package com.example.bugsgame.data

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.text.SimpleDateFormat
import java.util.Locale

object GoldPriceParser {
    fun parse(xml: String): GoldPrice {
        val parser = Xml.newPullParser()
        parser.setInput(xml.reader())

        var latestPrice: GoldPrice? = null

        var event = parser.eventType

        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG && parser.name.equals("Record", ignoreCase = true)) {
                val date = parser.getAttributeValue(null, "Date") ?: ""
                val code = parser.getAttributeValue(null, "Code") ?: ""

                if (code == "1") {
                    var buy = ""

                    while (parser.next() != XmlPullParser.END_TAG || !parser.name.equals("Record", ignoreCase = true)) {
                        if (parser.eventType == XmlPullParser.START_TAG && parser.name.equals("Buy", ignoreCase = true)) {
                            buy = parser.nextText().trim()
                        }
                    }

                    val price = buy.replace(',', '.').toDoubleOrNull()

                    if (price != null) {
                        val candidate = GoldPrice(price, date)
                        val previous = latestPrice

                        if (previous == null || compareDates(candidate.date, previous.date) > 0) {
                            latestPrice = candidate
                        }
                    }
                }
            }
            event = parser.next()
        }

        return latestPrice ?: throw IllegalStateException("В XML не найдена цена золота")
    }

    private fun compareDates(first: String, second: String): Int {
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
        format.isLenient = false

        val firstDate = format.parse(first)
        val secondDate = format.parse(second)

        return when {
            firstDate == null || secondDate == null -> first.compareTo(second)
            else -> firstDate.compareTo(secondDate)
        }
    }
}