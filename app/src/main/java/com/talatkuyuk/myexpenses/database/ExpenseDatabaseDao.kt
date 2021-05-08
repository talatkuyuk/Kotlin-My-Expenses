package com.talatkuyuk.myexpenses.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExpenseDatabaseDao {
    @Insert
    suspend fun insert(expense: Expense)

    @Update
    suspend fun update(expense: Expense)

    @Query("SELECT * from expense_table WHERE expenseId = :key")
    suspend fun get(key: Long): Expense?

    @Query("DELETE FROM expense_table")
    suspend fun clear()

    @Query("DELETE FROM expense_table WHERE expenseId = :key")
    suspend fun delete(key: Long)

    @Query("SELECT * FROM expense_table ORDER BY expenseId DESC LIMIT 1")
    suspend fun getLastExpense(): Expense?

    @Query("SELECT * FROM expense_table ORDER BY expenseId DESC")
    fun getAllExpenses(): LiveData<List<Expense>>
}