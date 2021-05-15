package com.talatkuyuk.myexpenses.screens.addexpense

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.talatkuyuk.myexpenses.R
import com.talatkuyuk.myexpenses.database.Expense
import com.talatkuyuk.myexpenses.database.ExpenseDatabase
import com.talatkuyuk.myexpenses.databinding.FragmentAddExpenseBinding
import com.talatkuyuk.myexpenses.enums.Category
import com.talatkuyuk.myexpenses.enums.Money
import com.talatkuyuk.myexpenses.utils.Utils
import kotlin.math.roundToInt

class AddExpenseFragment : Fragment() {

    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Get a reference to the binding object and inflate the fragment views.
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_expense, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = ExpenseDatabase.getInstance(application).expenseDatabaseDao

        val viewModelFactory = AddExpenseViewModelFactory(dataSource)

        val addExpenseViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(AddExpenseViewModel::class.java)

        binding.addExpenseViewModel = addExpenseViewModel

        addExpenseViewModel.navigateToMainTracker.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                this.findNavController().popBackStack(R.id.mainFragment, false)
                addExpenseViewModel.doneNavigating()
            }
        })

        binding.buttonAddExpense.setOnClickListener{
            val expense = Expense()

            expense.expenseTitle = binding.addExpenseTitle.text.toString()

            if (binding.addExpenseAmount.text.toString() == "") {
                expense.expenseAmount = 0
            } else {
                expense.expenseAmount = binding.addExpenseAmount.text.toString().toDouble().roundToInt()
            }

            expense.expenseCategory = when(binding.radioGroupCategory.checkedCheckableImageButtonId){
                binding.radioButtonCleaning.id -> Category.CLEANING.symbol
                binding.radioButtonClothing.id -> Category.CLOTHING.symbol
                binding.radioButtonEducation.id -> Category.EDUCATION.symbol
                binding.radioButtonFood.id -> Category.FOOD.symbol
                binding.radioButtonGoods.id -> Category.GOODS.symbol
                binding.radioButtonInvoice.id -> Category.INVOICE.symbol
                binding.radioButtonRent.id -> Category.RENT.symbol
                binding.radioButtonHealth.id -> Category.HEALTH.symbol
                else -> Category.OTHER.symbol
            }
            expense.expenseType = when(binding.radioGroupMoneyType.checkedRadioButtonId) {
                binding.radioButtonEuro.id -> Money.EURO.symbol
                binding.radioButtonDolar.id -> Money.DOLAR.symbol
                binding.radioButtonSterlin.id -> Money.STERLIN.symbol
                else -> Money.TL.symbol
            }

            var validationErrorMessage: String = ""

            if (expense.expenseTitle.length < 2) {
                validationErrorMessage = "Description must be minimum 2 characters. "
            }

            if (expense.expenseAmount == 0) {
                validationErrorMessage += "Amount should not be zero or empty"
            }

            if (validationErrorMessage == "") {
                addExpenseViewModel.onCreateExpense(expense)
            } else {
                Utils().showDialog(requireActivity(), validationErrorMessage)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}