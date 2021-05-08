package com.talatkuyuk.myexpenses.data.repository

import com.talatkuyuk.myexpenses.data.api.ApiHelper
import com.talatkuyuk.myexpenses.data.model.Converter

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun getCurrency(): Converter = apiHelper.getCurrency()

    suspend fun getExchangeRate(exchange: String): Converter  = apiHelper.getExchangeRate(exchange)
}