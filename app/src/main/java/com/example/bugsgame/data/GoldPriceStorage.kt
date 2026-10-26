package com.example.bugsgame.data

import android.content.Context
import androidx.core.content.edit

class GoldPriceStorage(context: Context) {
    private val preferences = context.getSharedPreferences(
        "gold_price_storage",
        Context.MODE_PRIVATE
    )

    fun save(goldPrice: GoldPrice) {
        preferences.edit {
            putFloat("price", goldPrice.price.toFloat())
            putString("date", goldPrice.date)
        }
    }

    fun get(): GoldPrice? {
        val price = preferences.getFloat("price", -1f)
        val date = preferences.getString("date", null)

        return if (price >= 0f && date != null) {
            GoldPrice(price.toDouble(), date)
        } else {
            null
        }
    }
}