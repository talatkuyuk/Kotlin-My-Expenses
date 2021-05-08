package com.talatkuyuk.myexpenses.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import kotlinx.serialization.*

@Serializable
@Entity(tableName = "expense_table")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    var expenseId: Long = 0L,

    @ColumnInfo(name = "category")
    var expenseCategory: String = "",

    @ColumnInfo(name = "title")
    var expenseTitle: String = "",

    @ColumnInfo(name = "amount")
    var expenseAmount: Int = 0,

    @ColumnInfo(name = "type")
    var expenseType: String = ""
)
