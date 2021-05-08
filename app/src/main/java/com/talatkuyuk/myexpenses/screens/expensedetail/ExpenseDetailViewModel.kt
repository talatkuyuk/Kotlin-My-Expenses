package com.talatkuyuk.myexpenses.screens.expensedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.talatkuyuk.myexpenses.database.ExpenseDatabaseDao
import kotlinx.coroutines.launch

class ExpenseDetailViewModel(
    val expenseKey: Long,
    val database: ExpenseDatabaseDao
) : ViewModel() {

    private val _navigateToMainTracker = MutableLiveData<Boolean?>()

    val navigateToMainTracker: LiveData<Boolean?>
        get() = _navigateToMainTracker

    fun doneNavigating() {
        _navigateToMainTracker.value = null
    }

    fun onDeleteExpense() {
        viewModelScope.launch {
            database.delete(expenseKey)

            // Setting this state variable to true will alert the observer and trigger navigation.
            _navigateToMainTracker.value = true
        }
    }

}