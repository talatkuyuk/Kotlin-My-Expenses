package com.talatkuyuk.myexpenses.screens.addexpense

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.talatkuyuk.myexpenses.database.Expense
import com.talatkuyuk.myexpenses.database.ExpenseDatabaseDao
import kotlinx.coroutines.launch

class AddExpenseViewModel(
    val database: ExpenseDatabaseDao) : ViewModel() {

        private val _navigateToMainTracker = MutableLiveData<Boolean?>()

        val navigateToMainTracker: LiveData<Boolean?>
            get() = _navigateToMainTracker

        fun doneNavigating() {
            _navigateToMainTracker.value = null
        }

        fun onCreateExpense(expense: Expense) {
            viewModelScope.launch {
                database.insert(expense)

                // Setting this state variable to true will alert the observer and trigger navigation.
                _navigateToMainTracker.value = true
            }
        }

    }
