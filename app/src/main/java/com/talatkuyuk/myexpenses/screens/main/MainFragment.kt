package com.talatkuyuk.myexpenses.screens.main

import android.content.SharedPreferences

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.talatkuyuk.myexpenses.R
import com.talatkuyuk.myexpenses.data.api.ApiHelper
import com.talatkuyuk.myexpenses.data.api.RetrofitBuilder
import com.talatkuyuk.myexpenses.database.Expense
import com.talatkuyuk.myexpenses.database.ExpenseDatabase
import com.talatkuyuk.myexpenses.databinding.FragmentMainBinding
import com.talatkuyuk.myexpenses.enums.Money
import com.talatkuyuk.myexpenses.utils.Utils
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class MainFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var binding: FragmentMainBinding

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)


        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //activity!!.onBackPressed()
                Utils().showDialogForExit(activity!!)
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main, container, false)
        val view = binding.root

        val application = requireNotNull(this.activity).application
        val dataSource = ExpenseDatabase.getInstance(application).expenseDatabaseDao
        val apiHelper = ApiHelper(RetrofitBuilder.apiService)

        val viewModelFactory = MainViewModelFactory(sharedPreferences, dataSource, apiHelper)

        mainViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(MainViewModel::class.java)

        binding.mainViewModel = mainViewModel

        binding.lifecycleOwner = viewLifecycleOwner

        val recyclerView: RecyclerView = binding.list

        recyclerView.layoutManager = LinearLayoutManager(context)

        mainViewModel.allExpensesConverted.observe(viewLifecycleOwner) { expenses ->
            //Toast.makeText(context, "allExpensesConverted updated", Toast.LENGTH_SHORT).show()
            expenses.let { recyclerView.adapter = MyRecyclerViewAdapter(it, mainViewModel.currencyType.value!!, {
                val action = MainFragmentDirections.actionMainFragmentToExpenseDetailFragment(
                    Json.encodeToString(it)
                )
                binding.root.findNavController().navigate(action)
            }) }
        }

        mainViewModel.isTL.observe(viewLifecycleOwner) { type ->
            type.let {
                Log.d("RETROFIT RESULT", it.toString())
            }
        }

        mainViewModel.currentConverter.observe(viewLifecycleOwner) {
            it.let {
                //Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                Log.d("CURRENT CONVERTER", it.toString())

                sharedPreferences.edit().putString("converter", Json.encodeToString(it)).apply();

            }
        }

        mainViewModel.response.observe(viewLifecycleOwner) {
            it.let {
                Log.d("RETROFIT RESULT", it)

            }
        }

        mainViewModel.currencyType.observe(viewLifecycleOwner) { type ->
            type.let {
                mainViewModel.updateExchangeRates()
                when(type) {
                    Money.TL.symbol -> {
                        binding.buttonTL.setTextColor(getResources().getColor(R.color.design_default_color_error))
                        binding.buttonEuro.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                        binding.buttonSterlin.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                        binding.buttonDolar.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                    }
                    Money.EURO.symbol -> {
                        binding.buttonTL.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                        binding.buttonEuro.setTextColor(getResources().getColor(R.color.design_default_color_error))
                        binding.buttonSterlin.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                        binding.buttonDolar.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                    }
                    Money.STERLIN.symbol -> {
                        binding.buttonTL.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                        binding.buttonEuro.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                        binding.buttonSterlin.setTextColor(getResources().getColor(R.color.design_default_color_error))
                        binding.buttonDolar.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                    }
                    Money.DOLAR.symbol -> {
                        binding.buttonTL.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                        binding.buttonEuro.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                        binding.buttonSterlin.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                        binding.buttonDolar.setTextColor(getResources().getColor(R.color.design_default_color_error))
                    }
                }
            }
        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setIdendity()

        binding.extendedFab.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToAddExpenseFragment()
            binding.root.findNavController().navigate(action)
        }

        //for test purposes
        //sharedPreferences.edit().putBoolean("isVisitedOnbording", false).apply()
        //sharedPreferences.edit().putString("name", "x").apply()

        binding.cardView.setOnClickListener{
            val action = MainFragmentDirections.actionMainFragmentToSettingsFragment()
            binding.root.findNavController().navigate(action)
        }
    }

    private fun setIdendity() {
        val name: String? = sharedPreferences.getString("name", "NoName")
        val gender: String? = sharedPreferences.getString("gender", "None")

        val identity: String = when (gender){
            "male" -> "Mr. " + name
            "female" -> "Mrs. " + name
            else -> "" + name
        }

        binding.textViewIdentity.text = identity
    }

}