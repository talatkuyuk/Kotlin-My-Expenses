package com.talatkuyuk.myexpenses.data.api

import com.talatkuyuk.myexpenses.data.model.Converter

class ApiHelper(private val apiService: ApiService) {
    suspend fun getCurrency(): Converter = apiService.getCurrency()

    suspend fun getExchangeRate(exchange: String): Converter  = apiService.getExchangeRate(exchange)
}