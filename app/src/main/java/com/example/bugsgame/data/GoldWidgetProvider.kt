package com.example.bugsgame.data

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.example.bugsgame.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class GoldWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = GoldPriceRepository(context)
                val goldPrice = repository.refreshPrice() ?: repository.getSavedPrice()

                val priceText = goldPrice?.let {
                    NumberFormat.getNumberInstance(Locale.forLanguageTag("ru-RU"))
                        .format(it.price) + " ₽"
                } ?: "Нет данных"

                appWidgetIds.forEach { widgetId ->
                    val views = RemoteViews(context.packageName, R.layout.gold_widget)

                    views.setTextViewText(R.id.goldPriceText, priceText)
                    appWidgetManager.updateAppWidget(widgetId, views)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}