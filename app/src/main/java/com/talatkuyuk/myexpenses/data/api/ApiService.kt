package com.talatkuyuk.myexpenses.data.api

import com.talatkuyuk.myexpenses.data.model.Converter
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("convert/?compact=ultra&apiKey=50fc9de4a729557262c5")
    suspend fun getExchangeRateOld(@Query("q") exchange: String) : Converter

    @GET("{exchange}")
    suspend fun getExchangeRate(@Path("exchange") exchange: String) : Converter

    @GET("USD")
    suspend fun getCurrency(): Converter
}