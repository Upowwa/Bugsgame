package com.example.bugsgame.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoldPriceRepository(context: Context) {
    private val storage = GoldPriceStorage(context.applicationContext)

    suspend fun refreshPrice(): GoldPrice? {
        return withContext(Dispatchers.IO) {
            try {
                val goldPrice = GoldApiClient.getLatestGoldPrice()
                storage.save(goldPrice)
                goldPrice
            } catch (e: Exception) {
                storage.get()
            }
        }
    }

    fun getSavedPrice(): GoldPrice? {
        return storage.get()
    }
}