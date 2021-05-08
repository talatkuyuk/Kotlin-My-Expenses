package com.talatkuyuk.myexpenses.data.model

import com.google.gson.annotations.SerializedName

data class Converter(
    //@SerializedName("xx")
    val TRY_USD: Double = 0.0,
    val TRY_GBP: Double = 0.0,
    val TRY_EUR: Double = 0.0,
    val GBP_USD: Double = 0.0,
    var GBP_TRY: Double = 0.0,
    val GBP_EUR: Double = 0.0,
    val EUR_USD: Double = 0.0,
    var EUR_TRY: Double = 0.0,
    val EUR_GBP: Double = 0.0,
    var USD_TRY: Double = 0.0,
    val USD_GBP: Double = 0.0,
    val USD_EUR: Double = 0.0
)