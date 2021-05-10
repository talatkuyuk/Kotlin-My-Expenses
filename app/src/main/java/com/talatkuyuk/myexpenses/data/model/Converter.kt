package com.talatkuyuk.myexpenses.data.model

import com.google.gson.annotations.SerializedName
import com.talatkuyuk.myexpenses.enums.Money
import kotlinx.serialization.Serializable

@Serializable
data class Converter(
    //@SerializedName("xx")
    var TRY_USD: Double = 0.0,
    var TRY_GBP: Double = 0.0,
    var TRY_EUR: Double = 0.0,
    var GBP_USD: Double = 0.0,
    var GBP_TRY: Double = 0.0,
    var GBP_EUR: Double = 0.0,
    var EUR_USD: Double = 0.0,
    var EUR_TRY: Double = 0.0,
    var EUR_GBP: Double = 0.0,
    var USD_TRY: Double = 0.0,
    var USD_GBP: Double = 0.0,
    var USD_EUR: Double = 0.0
) {

    companion object Factory {
        val neutral = Converter(
            1.0, 1.0, 1.0,
            1.0, 1.0, 1.0,
            1.0, 1.0, 1.0,
            1.0, 1.0, 1.0)

        fun fun1(str: String): Converter {
            // create your logic
            return Converter()
        }

        fun fun2(i: Int): Converter = Converter(/*create your params*/)
    }

    fun hasValidPart(currencyType: String): Boolean {
        var bool: Boolean = true
        when(currencyType) {
            Money.TL.symbol -> {
                if (this.EUR_TRY == 1.0) { bool = false }
                if (this.GBP_TRY == 1.0) { bool = false }
                if (this.USD_TRY == 1.0) { bool = false }
            }
            Money.EURO.symbol -> {
                if (this.TRY_EUR == 1.0) { bool = false }
                if (this.GBP_EUR == 1.0) { bool = false }
                if (this.USD_EUR == 1.0) { bool = false }
            }
            Money.STERLIN.symbol -> {
                if (this.TRY_GBP == 1.0) { bool = false }
                if (this.EUR_GBP == 1.0) { bool = false }
                if (this.USD_GBP == 1.0) { bool = false }
            }
            Money.DOLAR.symbol -> {
                if (this.TRY_USD == 1.0) { bool = false }
                if (this.GBP_USD == 1.0) { bool = false }
                if (this.EUR_USD == 1.0) { bool = false }
            }
        }
        return bool
    }


    fun isNeutral () : Boolean {
        var bool: Boolean = true
        if (this.TRY_USD != 1.0) { bool = false }
        if (this.TRY_GBP != 1.0) { bool = false }
        if (this.TRY_EUR != 1.0) { bool = false }
        if (this.GBP_USD != 1.0) { bool = false }
        if (this.GBP_TRY != 1.0) { bool = false }
        if (this.GBP_EUR != 1.0) { bool = false }
        if (this.EUR_USD != 1.0) { bool = false }
        if (this.EUR_TRY != 1.0) { bool = false }
        if (this.EUR_GBP != 1.0) { bool = false }
        if (this.USD_TRY != 1.0) { bool = false }
        if (this.USD_GBP != 1.0) { bool = false }
        if (this.USD_EUR != 1.0) { bool = false }
        return bool
    }

    fun mergeMutable(converter: Converter) {
        if (converter.TRY_EUR != 0.0) this.TRY_EUR = converter.TRY_EUR
        if (converter.TRY_USD != 0.0) this.TRY_USD = converter.TRY_USD
        if (converter.TRY_GBP != 0.0) this.TRY_GBP = converter.TRY_GBP
        if (converter.USD_EUR != 0.0) this.USD_EUR = converter.USD_EUR
        if (converter.USD_GBP != 0.0) this.USD_GBP = converter.USD_GBP
        if (converter.USD_TRY != 0.0) this.USD_TRY = converter.USD_TRY
        if (converter.EUR_GBP != 0.0) this.EUR_GBP = converter.EUR_GBP
        if (converter.EUR_TRY != 0.0) this.EUR_TRY = converter.EUR_TRY
        if (converter.EUR_USD != 0.0) this.EUR_USD = converter.EUR_USD
        if (converter.GBP_EUR != 0.0) this.GBP_EUR = converter.GBP_EUR
        if (converter.GBP_TRY != 0.0) this.GBP_TRY = converter.GBP_TRY
        if (converter.GBP_USD != 0.0) this.GBP_USD = converter.GBP_USD
    }
}