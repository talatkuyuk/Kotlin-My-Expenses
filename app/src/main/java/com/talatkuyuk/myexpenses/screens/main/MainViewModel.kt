package com.talatkuyuk.myexpenses.screens.main

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.*
import com.talatkuyuk.myexpenses.data.model.Converter
import com.talatkuyuk.myexpenses.data.repository.MainRepository
import com.talatkuyuk.myexpenses.database.Expense
import com.talatkuyuk.myexpenses.database.ExpenseDatabaseDao
import com.talatkuyuk.myexpenses.enums.Money
import com.talatkuyuk.myexpenses.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import java.lang.Exception
import kotlin.math.roundToInt
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor


class MainViewModel(
    val database: ExpenseDatabaseDao,
    private val mainRepository: MainRepository
) : ViewModel() {


    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    private val _currentConverter = MutableLiveData<Converter>()
    val currentConverter: LiveData<Converter>
        get() = _currentConverter

    private val _currencyType = MutableLiveData<String>()
    val currencyType: LiveData<String>
        get() = _currencyType


    // I tried to listen in XML, I got errors
    val isTL: LiveData<Boolean> = Transformations.map(_currencyType) {
        return@map it == Money.TL.symbol
    }

    init {
        _currencyType.value = Money.TL.symbol
    }


    var allExpenses: LiveData<List<Expense>> = database.getAllExpenses()


    val allExpensesConverted: LiveData<List<Expense>> =
        Transformations.switchMap(currentConverter) { getAllExpensesConverted(it) }

    val totalAmount: LiveData<String> = Transformations.map(allExpensesConverted) { expenses ->
        var total: Int = 0
        for (expense in expenses) {
            total += expense.expenseAmount
        }
        return@map total.toString()
    }


    fun getAllExpensesConverted(converter: Converter) =
        allExpenses.map {
            val newList: List<Expense> = it

            for (expense in newList) {
                when (currencyType.value) {
                    Money.EURO.symbol -> {
                        when (expense.expenseType) {
                            Money.TL.symbol -> expense.expenseAmount =
                                (expense.expenseAmount * converter.TRY_EUR).roundToInt()
                            Money.DOLAR.symbol -> expense.expenseAmount =
                                (expense.expenseAmount * converter.USD_EUR).roundToInt()
                            Money.STERLIN.symbol -> expense.expenseAmount =
                                (expense.expenseAmount * converter.GBP_EUR).roundToInt()
                            else -> expense.expenseAmount =
                                expense.expenseAmount // it request else part, what to do :)
                        }
                    }
                    Money.STERLIN.symbol -> {
                        when (expense.expenseType) {
                            Money.TL.symbol -> expense.expenseAmount =
                                (expense.expenseAmount * converter.TRY_GBP).roundToInt()
                            Money.DOLAR.symbol -> expense.expenseAmount =
                                (expense.expenseAmount * converter.USD_GBP).roundToInt()
                            Money.EURO.symbol -> expense.expenseAmount =
                                (expense.expenseAmount * converter.EUR_GBP).roundToInt()
                            else -> expense.expenseAmount =
                                expense.expenseAmount // it request else part, what to do :)
                        }
                    }
                    Money.DOLAR.symbol -> {
                        when (expense.expenseType) {
                            Money.TL.symbol -> expense.expenseAmount =
                                (expense.expenseAmount * converter.TRY_USD).roundToInt()
                            Money.STERLIN.symbol -> expense.expenseAmount =
                                (expense.expenseAmount * converter.GBP_USD).roundToInt()
                            Money.EURO.symbol -> expense.expenseAmount =
                                (expense.expenseAmount * converter.EUR_USD).roundToInt()
                            else -> expense.expenseAmount =
                                expense.expenseAmount // it request else part, what to do :)
                        }
                    }
                    else -> {
                        when (expense.expenseType) {
                            Money.STERLIN.symbol -> expense.expenseAmount =
                                (expense.expenseAmount * converter.GBP_TRY).roundToInt()
                            Money.DOLAR.symbol -> expense.expenseAmount =
                                (expense.expenseAmount * converter.USD_TRY).roundToInt()
                            Money.EURO.symbol -> expense.expenseAmount =
                                (expense.expenseAmount * converter.EUR_TRY).roundToInt()
                            else -> expense.expenseAmount =
                                expense.expenseAmount // it request else part, what to do :)
                        }
                    }
                }

            }
            return@map newList
        }









    public fun updateExchangeRates() {
        when (_currencyType.value) {
            Money.EURO.symbol -> getExchangeRate("EUR")
            Money.STERLIN.symbol -> getExchangeRate("GBP")
            Money.DOLAR.symbol -> getExchangeRate("USD")
            else -> getExchangeRate("TRY")
        }
    }

    public fun makeCurrencyTypeTL() {
        _currencyType.value = Money.TL.symbol
    }
    public fun makeCurrencyTypeEuro() {
        _currencyType.value = Money.EURO.symbol
    }
    public fun makeCurrencyTypeDolar() {
        _currencyType.value = Money.DOLAR.symbol
    }
    public fun makeCurrencyTypeSterlin() {
        _currencyType.value = Money.STERLIN.symbol
    }

    private fun getExpenses(expense: Expense) {
        viewModelScope.launch {
            allExpenses = database.getAllExpenses()
        }
    }

    //Repository pattern worked :)
    fun getExchangeRate(exchange: String) {
        viewModelScope.launch {
            try {
                _currentConverter.value = mainRepository.getExchangeRate(exchange)
                _response.value = "OK"
            } catch (e: Exception) {
                _response.value = "Failure: ${e.message}"

                // Yapmaya çalıştım ama olmadı.
                //val converterString: String? = sharedPref.getString("converter", "")
                //val converter: Converter = Json.decodeFromString<Converter>(converterString!!)
                //_currentConverter.value = converter
            }
        }
    }

    //Callback example, I keep it just for memo
    fun getExchangeRateXX(exchange: String, callback: (Double) -> Unit) {
        viewModelScope.launch {
            try {
                val converter: Converter = mainRepository.getExchangeRate(exchange)
                val value: Double? = converter.getField<Double>(exchange)
                return@launch callback(value!!)
            } catch (e: Exception) {
                return@launch callback(0.0)
            }
        }
    }

    // getting a field from an object with reflection
    @Throws(IllegalAccessException::class, ClassCastException::class)
    inline fun <reified T> Any.getField(fieldName: String): T? {
        this::class.memberProperties.forEach { kCallable ->
            if (fieldName == kCallable.name) {
                return kCallable.getter.call(this) as T?
            }
        }
        return null
    }

    // merging two objects with reflection
    infix inline fun <reified T : Any> T.merge(other: T): T {
        val nameToProperty = T::class.declaredMemberProperties.associateBy { it.name }
        val primaryConstructor = T::class.primaryConstructor!!
        val args = primaryConstructor.parameters.associate { parameter ->
            val property = nameToProperty[parameter.name]!!
            parameter to (property.get(other) ?: property.get(this))
        }
        return primaryConstructor.callBy(args)
    }

    // https://blog.mindorks.com/using-retrofit-with-kotlin-coroutines-in-android
    // Actually it is not needed, I can implement it later as pure LiveData
    fun getCurrencyx() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            //emit(Resource.success(data = mainRepository.getCurrency()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    //ApiInterface didn't work ! I keep it and this function just for memo
    private fun getExchangeRateX(exchange: String) {
        viewModelScope.launch {
            try {
                val result: Call<String> = ApiInterface.create().getExchangeRate(exchange)
                //_response.value = result.toString()
            } catch (e: Exception) {
                //_response.value = "Failure: ${e.message}"
            }
        }
    }

}