package com.talatkuyuk.myexpenses.screens.main

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.*
import com.talatkuyuk.myexpenses.data.model.Converter
import com.talatkuyuk.myexpenses.data.repository.MainRepository
import com.talatkuyuk.myexpenses.data.repository.PreferenceRepository
import com.talatkuyuk.myexpenses.database.Expense
import com.talatkuyuk.myexpenses.database.ExpenseDatabaseDao
import com.talatkuyuk.myexpenses.enums.Money
import com.talatkuyuk.myexpenses.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import retrofit2.Call
import java.lang.Exception
import java.text.DecimalFormat
import kotlin.math.roundToInt
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor


class MainViewModel(
    val application: Application,
    val database: ExpenseDatabaseDao,
    private val mainRepository: MainRepository
) : ViewModel() {

    //private val sharedpreferences: SharedPreferences  = application.getSharedPreferences("ExpenseShare", Context.MODE_PRIVATE)
    private val preferenceRepository by lazy { PreferenceRepository(application.applicationContext) }

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

    val allExpenses: LiveData<List<Expense>> = Transformations.switchMap(
        response,
        { value -> getConvertedExpenses(value) }
    )

    private fun getConvertedExpenses(string: String)
        : LiveData<List<Expense>> = database.getAllExpenses().map { originalExpenseList ->

            if (currencyType.value == "") {
                return@map originalExpenseList

            } else {
                originalExpenseList.forEach {
                    it.expenseAmount = getProperAmount(currencyType.value!!, it, currentConverter.value!!)
                }
                return@map originalExpenseList
            }
    }

    val countExpenses: LiveData<Int> = Transformations.map(allExpenses) { expenses -> return@map expenses?.size ?: 0 }

    val totalAmount: LiveData<String> = Transformations.map(allExpenses) { expenses ->
        var total: Int = 0

        for (expense in expenses) {
            total += expense.expenseAmount
        }

        val dec = DecimalFormat("#,###")
        val formattedTotal = dec.format(total)

        if (currencyType.value == "") {
            return@map "Select ${Money.TL.symbol}-${Money.STERLIN.symbol}-${Money.EURO.symbol}-${Money.DOLAR.symbol}"
        } else {
            return@map "${formattedTotal} ${currencyType.value}"
        }
    }


    init {
        _currencyType.value = ""
        _currentConverter.value = preferenceRepository.getConverter()
        Log.d("VIEWMODEL INI CONVERTER", currentConverter.value.toString())
    }


    public fun updateExchangeRates() {
        when (_currencyType.value) {
            Money.EURO.symbol -> getExchangeRate("EUR")
            Money.STERLIN.symbol -> getExchangeRate("GBP")
            Money.DOLAR.symbol -> getExchangeRate("USD")
            Money.TL.symbol  -> getExchangeRate("TRY")
            else -> getExchangeRate("")
        }
    }


    public fun makeCurrencyTypeEmpty() {
        if (currencyType.value != "")
            _currencyType.value = ""
    }
    public fun makeCurrencyTypeTL() {
        if (currencyType.value != Money.TL.symbol)
            _currencyType.value = Money.TL.symbol
    }
    public fun makeCurrencyTypeEuro() {
        if (currencyType.value != Money.EURO.symbol)
            _currencyType.value = Money.EURO.symbol
    }
    public fun makeCurrencyTypeDolar() {
        if (currencyType.value != Money.DOLAR.symbol)
            _currencyType.value = Money.DOLAR.symbol
    }
    public fun makeCurrencyTypeSterlin() {
        if (currencyType.value != Money.STERLIN.symbol)
            _currencyType.value = Money.STERLIN.symbol
    }

    //Repository pattern worked :)
    fun getExchangeRate(exchange: String) {
        viewModelScope.launch {
            try {
                val current: Converter = currentConverter.value!!
                val new: Converter = mainRepository.getExchangeRate(exchange)
                current.mergeMutable(new)
                _currentConverter.value = current
                _response.value = Json.encodeToString(current)
            } catch (e: Exception) {
                _response.value = "Failure: ${e.message}"
            }
        }
    }


    fun getProperAmount(currency: String, expense: Expense, converter: Converter): Int {
        var converted : Int = expense.expenseAmount
        when (currency) {
            Money.EURO.symbol -> {
                when (expense.expenseType) {
                    Money.TL.symbol -> converted = (expense.expenseAmount * converter.TRY_EUR).roundToInt()
                    Money.DOLAR.symbol -> converted = (expense.expenseAmount * converter.USD_EUR).roundToInt()
                    Money.STERLIN.symbol -> converted = (expense.expenseAmount * converter.GBP_EUR).roundToInt()
                    Money.EURO.symbol -> converted = expense.expenseAmount
                }
            }
            Money.STERLIN.symbol -> {
                when (expense.expenseType) {
                    Money.TL.symbol -> converted = (expense.expenseAmount * converter.TRY_GBP).roundToInt()
                    Money.DOLAR.symbol -> converted = (expense.expenseAmount * converter.USD_GBP).roundToInt()
                    Money.EURO.symbol -> converted = (expense.expenseAmount * converter.EUR_GBP).roundToInt()
                    Money.STERLIN.symbol -> converted = expense.expenseAmount
                }
            }
            Money.DOLAR.symbol -> {
                when (expense.expenseType) {
                    Money.TL.symbol -> converted = (expense.expenseAmount * converter.TRY_USD).roundToInt()
                    Money.STERLIN.symbol -> converted = (expense.expenseAmount * converter.GBP_USD).roundToInt()
                    Money.EURO.symbol -> converted = (expense.expenseAmount * converter.EUR_USD).roundToInt()
                    Money.DOLAR.symbol -> converted = expense.expenseAmount
                }
            }
            Money.TL.symbol -> {
                when (expense.expenseType) {
                    Money.STERLIN.symbol -> converted = (expense.expenseAmount * converter.GBP_TRY).roundToInt()
                    Money.DOLAR.symbol -> converted = (expense.expenseAmount * converter.USD_TRY).roundToInt()
                    Money.EURO.symbol -> converted = (expense.expenseAmount * converter.EUR_TRY).roundToInt()
                    Money.TL.symbol -> converted = expense.expenseAmount
                }
            }
            else -> converted = expense.expenseAmount
        }
        return converted
    }

    //Callback example, I keep it just for memo
    fun getExchangeRateWithCallback(exchange: String, callback: (Double) -> Unit) {
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