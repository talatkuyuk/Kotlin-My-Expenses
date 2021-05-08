package com.talatkuyuk.myexpenses.screens.addexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.talatkuyuk.myexpenses.database.ExpenseDatabaseDao

class AddExpenseViewModelFactory(
    private val dataSource: ExpenseDatabaseDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddExpenseViewModel::class.java)) {
            return AddExpenseViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}