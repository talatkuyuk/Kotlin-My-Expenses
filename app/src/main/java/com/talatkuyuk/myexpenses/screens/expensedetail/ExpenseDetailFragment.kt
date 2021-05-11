package com.talatkuyuk.myexpenses.screens.expensedetail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.talatkuyuk.myexpenses.R
import com.talatkuyuk.myexpenses.database.Expense
import com.talatkuyuk.myexpenses.database.ExpenseDatabase
import com.talatkuyuk.myexpenses.databinding.FragmentExpenseDetailBinding
import com.talatkuyuk.myexpenses.enums.Category
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.DecimalFormat

class ExpenseDetailFragment : Fragment() {

    private lateinit var binding: FragmentExpenseDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Get a reference to the binding object and inflate the fragment views.
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_expense_detail, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = ExpenseDatabase.getInstance(application).expenseDatabaseDao

        val args: ExpenseDetailFragmentArgs by navArgs()
        val expense: Expense = Json.decodeFromString<Expense>(args.expense)

        val viewModelFactory = ExpenseDetailViewModelFactory(expense.expenseId, dataSource)

        val expenseDetailViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(ExpenseDetailViewModel::class.java)

        expenseDetailViewModel.navigateToMainTracker.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                this.findNavController().popBackStack(R.id.mainFragment, false)
                expenseDetailViewModel.doneNavigating()
            }
        })

        binding.deleteButtonForDetail.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Are you sure you want to delete?")
                .setTitle("Delete The Expence")
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog, which ->

                    expenseDetailViewModel.onDeleteExpense()

                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val args: ExpenseDetailFragmentArgs by navArgs()
        val expense: Expense = Json.decodeFromString<Expense>(args.expense)
        val currencyType = args.currencyType

        binding.imageCategoryForDetail.setImageResource(when(expense.expenseCategory){
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

        val dec = DecimalFormat("#,###")
        val formattedAmount = dec.format(expense.expenseAmount)

        binding.textDescForDetail.setText(expense.expenseTitle)
        binding.textAmountForDetail.setText(formattedAmount.toString() + currencyType)
    }
}