package com.talatkuyuk.myexpenses.screens.main

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.talatkuyuk.myexpenses.R
import com.talatkuyuk.myexpenses.data.model.Converter
import com.talatkuyuk.myexpenses.database.Expense
import com.talatkuyuk.myexpenses.enums.Category
import com.talatkuyuk.myexpenses.enums.Money

import com.talatkuyuk.myexpenses.screens.main.DummyContent.DummyItem
import java.text.DecimalFormat
import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.properties.Delegates


class MyRecyclerViewAdapter(
    private val values: List<Expense>,
    private val currentType: String,
    private val listener: (Expense) -> Unit
) : RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.expense_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        holder.titleView.text = item.expenseTitle

        val dec = DecimalFormat("#,###")
        val formattedAmount = dec.format(item.expenseAmount)

        if (currentType == "") {
            holder.priceView.text = "${formattedAmount} ${item.expenseType}"
        } else {
            holder.priceView.text = "${formattedAmount} ${currentType}"
        }

        holder.categoryView.setImageResource(when(item.expenseCategory){
            Category.CLEANING.symbol -> R.drawable.category_cleaning
            Category.CLOTHING.symbol -> R.drawable.category_clothing
            Category.EDUCATION.symbol -> R.drawable.category_education
            Category.FOOD.symbol -> R.drawable.category_food
            Category.GOODS.symbol -> R.drawable.category_goods
            Category.INVOICE.symbol -> R.drawable.category_invoice
            Category.RENT.symbol -> R.drawable.category_rent
            Category.HEALTH.symbol -> R.drawable.category_health
            else -> R.drawable.category_other
        })

        holder.cardView.setOnClickListener { listener(item) }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: View = view.findViewById(R.id.item_card)
        val categoryView: ImageView = view.findViewById(R.id.item_image)
        val titleView: TextView = view.findViewById(R.id.item_name)
        val priceView: TextView = view.findViewById(R.id.item_price)

        override fun toString(): String {
            return super.toString() + " '" + titleView.text + "'"
        }
    }

    fun getProperAmount(currencyType: String, expense: Expense, converter: Converter): Int {
            var converted : Int = 999
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
}
