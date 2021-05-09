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

    private val sharedpreferences: SharedPreferences  = application.getSharedPreferences("ExpenseShare", Context.MODE_PRIVATE)

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

    var allExpenses: LiveData<List<Expense>> = database.getAllExpenses()

    val allExpensesConverted: LiveData<List<Expense>> = Transformations.switchMap(response,
        { res -> getConvertedExpenses(res) })

    private fun getConvertedExpenses(string: String)
        : LiveData<List<Expense>> = Transformations.map(allExpenses)  { originalExpenseList ->

            if (currencyType.value == "") {
                return@map originalExpenseList
                // allExpensenses, yani bu fonksiyona giren originalExpenseList aslında hep orjinal kalıyor, onda sıkıntı yok. Kontrol ettim.
                // Herhangi bir para birimi seçilmediğinde aslında allExpenses LiveData'sına tekrar bakıp orjinal listenin aynısını döndürmesi lazım
                // amma-velakin neden döndürmüyor da hala allExpensesConverted'ın son halini kale alıyor onu çözemedim.
                // Rakamlardaki sıkıntı bu fonksiyondan kaynaklanıyor. Ve tabiki aşağıdaki else part'ta da aynı sıkıntı var.
                // Sonuçta rakamlar WEIRD oldu, çözemedim. Aslında bu fonksiyonu doğru çattığımı düşünüyorum.

            } else {
                originalExpenseList.forEach {
                    it.expenseAmount = getProperAmount(currencyType.value!!, it, currentConverter.value!!)
                }
                Log.d("getConvertedExpenses", allExpenses.toString())
                return@map originalExpenseList

                // Aşağıdaki kod da işe yaramadı, yeni bir listeye aktarayım dedim ama bu da kâr etmedi.
                //val liveData: LiveData<List<Expense>> = MutableLiveData<List<Expense>>()
                /*val list  = mutableListOf<Expense>()
                for (expense in it) {
                    expense.let {
                        val x = expense
                        x.expenseAmount = getProperAmount(_currencyType.value!!, expense, converter)
                        list.add(x)
                    }
                }*/
                //return@map it
            }

    }

    val totalAmount: LiveData<String> = Transformations.map(allExpensesConverted) { expenses ->
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
        val con = sharedpreferences.getString("Converter", Json.encodeToString(Converter.neutral))
        _currentConverter.value = con?.let { Json.decodeFromString<Converter>(it) }
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

    private fun getExpenses(expense: Expense) {
        viewModelScope.launch {
            allExpenses = database.getAllExpenses()
        }
    }

    //Repository pattern worked :)
    fun getExchangeRate(exchange: String) {
        viewModelScope.launch {
            try {
                val cur: Converter = currentConverter.value!!
                val new: Converter = mainRepository.getExchangeRate(exchange)
                cur.mergeMutable(new)
                _currentConverter.value = cur
                _response.value = Json.encodeToString(cur)
            } catch (e: Exception) {
                _response.value = "Failure: ${e.message}"
            }
        }
    }


    fun getProperAmount(currencyType: String, expense: Expense, converter: Converter): Int {
        var converted : Int = expense.expenseAmount
        when (currencyType) {
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