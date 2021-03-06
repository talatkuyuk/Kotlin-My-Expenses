package com.talatkuyuk.myexpenses.screens.main

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.talatkuyuk.myexpenses.data.api.ApiHelper
import com.talatkuyuk.myexpenses.data.repository.MainRepository
import com.talatkuyuk.myexpenses.database.ExpenseDatabaseDao

class MainViewModelFactory(
    private val application: Application,
    private val dataSource: ExpenseDatabaseDao,
    private val apiHelper: ApiHelper
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application, dataSource, MainRepository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
