package com.talatkuyuk.myexpenses.screens.main

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.talatkuyuk.myexpenses.R
import com.talatkuyuk.myexpenses.database.Expense
import com.talatkuyuk.myexpenses.enums.Category

import com.talatkuyuk.myexpenses.screens.main.DummyContent.DummyItem


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
        holder.priceView.text = item.expenseAmount.toString() + currentType

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
}