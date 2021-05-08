package com.talatkuyuk.myexpenses

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.talatkuyuk.myexpenses.database.Expense
import com.talatkuyuk.myexpenses.database.ExpenseDatabase
import com.talatkuyuk.myexpenses.database.ExpenseDatabaseDao
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException

import kotlinx.coroutines.runBlocking


@RunWith(AndroidJUnit4::class)
class SleepDatabaseTest {

    private lateinit var expenseDao: ExpenseDatabaseDao
    private lateinit var db: ExpenseDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        //val context: Context = ApplicationProvider.getApplicationContext()

        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, ExpenseDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        expenseDao = db.expenseDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetExpense() {
        runBlocking {
            var newexpense = Expense()
            expenseDao.insert(newexpense)
            val expense = expenseDao.getLastExpense()
            assertEquals(expense?.expenseCategory, 0)
        }
    }
}