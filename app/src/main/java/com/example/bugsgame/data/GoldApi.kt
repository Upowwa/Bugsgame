package com.example.bugsgame.data

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

interface GoldApi {
    @GET("scripts/xml_metall.asp")
    suspend fun getGoldPrices(
        @Query("date_req1") startDate: String,
        @Query("date_req2") endDate: String
    ): String
}

object GoldApiClient {
    private val api: GoldApi = Retrofit.Builder()
        .baseUrl("https://www.cbr.ru/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
        .create(GoldApi::class.java)

    suspend fun getLatestGoldPrice(): GoldPrice {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
        val endDate = Calendar.getInstance()
        val startDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -30)
        }

        val xml = api.getGoldPrices(
            startDate = formatter.format(startDate.time),
            endDate = formatter.format(endDate.time)
        )

        android.util.Log.d("GoldPriceTest", "Ответ сервера: ${xml.take(1000)}")

        return GoldPriceParser.parse(xml)
    }
}