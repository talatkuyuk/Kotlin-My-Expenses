package com.talatkuyuk.myexpenses.screens.main

import android.content.Context
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
import androidx.recyclerview.widget.RecyclerView
import com.talatkuyuk.myexpenses.R
import com.talatkuyuk.myexpenses.data.api.ApiHelper
import com.talatkuyuk.myexpenses.data.api.RetrofitBuilder
import com.talatkuyuk.myexpenses.data.repository.PreferenceRepository
import com.talatkuyuk.myexpenses.database.ExpenseDatabase
import com.talatkuyuk.myexpenses.databinding.FragmentMainBinding
import com.talatkuyuk.myexpenses.enums.Money
import com.talatkuyuk.myexpenses.utils.Utils
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class MainFragment : Fragment() {

    //private lateinit var sharedPreferences: SharedPreferences
    private val preferenceRepository by lazy { PreferenceRepository(requireContext()) }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main, container, false)
        val view = binding.root

        val application = requireNotNull(this.activity).application
        val dataSource = ExpenseDatabase.getInstance(application).expenseDatabaseDao
        val apiHelper = ApiHelper(RetrofitBuilder.apiService)

        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val viewModelFactory = MainViewModelFactory(application, dataSource, apiHelper)

        //progressbar = BtnLoadingProgressbar(view)
        //progressbar.setLoading()
        //progressbar.setState(true){}

        mainViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(MainViewModel::class.java)

        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val recyclerView: RecyclerView = binding.list
        recyclerView.layoutManager = LinearLayoutManager(context)

        // hides when it is scrooling down to see the content below
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy<0 && !binding.extendedFab.isShown) {
                    binding.extendedFab.show()
                } else if (dy>0 && binding.extendedFab.isShown) {
                    binding.extendedFab.hide()
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        mainViewModel.allExpenses.observe(viewLifecycleOwner) { expenses ->
            expenses.let {
                //Log.d("CURRENT EXPENSE LIST", it.toString())

                var currencyType = mainViewModel.currencyType.value!!

                recyclerView.adapter = MyRecyclerViewAdapter(
                    it,
                    currencyType,
                    {
                        if (currencyType == "") {
                            val action = MainFragmentDirections.actionMainFragmentToExpenseDetailFragment(
                                Json.encodeToString(it), it.expenseType)
                            binding.root.findNavController().navigate(action)
                        } else {
                            val action = MainFragmentDirections.actionMainFragmentToExpenseDetailFragment(
                                Json.encodeToString(it), currencyType)
                            binding.root.findNavController().navigate(action)
                        }
                    }
                )
            }
        }

        mainViewModel.isTL.observe(viewLifecycleOwner) {
            it?.let {
                //Log.d("TL BUTTON SELECTED", it.toString())
            }
        }

        mainViewModel.progressBar.observe(viewLifecycleOwner) {
                if (it) {
                    binding.progressBar.visibility = View.VISIBLE
                    Log.d("PROGRESS BAR: ", it.toString())
                }
                else {
                    binding.progressBar.visibility = View.INVISIBLE
                    Log.d("PROGRESS BAR: ", it.toString())
                }
        }

        mainViewModel.currentConverter.observe(viewLifecycleOwner) {
            it?.let {
                Log.d("CURRENT CONVERTER", it.toString())
                if (!it.isNeutral())
                    preferenceRepository.setConverter(it)
            }
        }

        mainViewModel.response.observe(viewLifecycleOwner) {
            it?.let {
                Log.d("RETROFIT RESULT", it)
                val converter = mainViewModel.currentConverter.value!!
                if (!converter.hasValidPart(mainViewModel.currencyType.value!!)) {
                    var message = "No available Exchange Rates."
                    if (!Utils().isOnline(requireContext())){
                        message += " This may cause because you are offline."
                    } else {
                        message += " This may cause because the API service is broken."
                    }
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    //Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("OK", {}).show()
                }
            }
        }

        mainViewModel.countExpenses.observe(viewLifecycleOwner) { count ->
            if (  count == 0) {
                binding.textViewNoContent.visibility = View.VISIBLE
            } else {
                binding.textViewNoContent.visibility = View.GONE
            }
        }

        mainViewModel.currencyType.observe(viewLifecycleOwner) { type ->
            type.let {
                mainViewModel.updateExchangeRates()
                arrangeButtonColors(it)
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        setIdendity()

        binding.extendedFab.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToAddExpenseFragment()
            binding.root.findNavController().navigate(action)
        }

        //for test purposes
        //preferenceRepository.setIsVisitedOnboarding(false)
        //preferenceRepository.setName("")

        binding.cardView.setOnClickListener{
            val action = MainFragmentDirections.actionMainFragmentToSettingsFragment()
            binding.root.findNavController().navigate(action)
        }
    }

    private fun setIdendity() {
        val name: String = preferenceRepository.getName()
        val gender: String = preferenceRepository.getGender()

        val identity: String = when (gender){
            "male" -> "Mr. " + name
            "female" -> "Mrs. " + name
            else -> "" + name
        }

        binding.textViewIdentity.text = identity
    }


    private fun arrangeButtonColors(type: String) {
        when(type) {
            Money.TL.symbol -> {
                binding.buttonTL.setTextColor(getResources().getColor(R.color.design_default_color_error))
                binding.buttonEuro.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                binding.buttonSterlin.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                binding.buttonDolar.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                binding.buttonOriginal.setTextColor(getResources().getColor(R.color.design_default_color_primary))
            }
            Money.EURO.symbol -> {
                binding.buttonTL.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                binding.buttonEuro.setTextColor(getResources().getColor(R.color.design_default_color_error))
                binding.buttonSterlin.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                binding.buttonDolar.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                binding.buttonOriginal.setTextColor(getResources().getColor(R.color.design_default_color_primary))
            }
            Money.STERLIN.symbol -> {
                binding.buttonTL.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                binding.buttonEuro.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                binding.buttonSterlin.setTextColor(getResources().getColor(R.color.design_default_color_error))
                binding.buttonDolar.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                binding.buttonOriginal.setTextColor(getResources().getColor(R.color.design_default_color_primary))
            }
            Money.DOLAR.symbol -> {
                binding.buttonTL.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                binding.buttonEuro.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                binding.buttonSterlin.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                binding.buttonDolar.setTextColor(getResources().getColor(R.color.design_default_color_error))
                binding.buttonOriginal.setTextColor(getResources().getColor(R.color.design_default_color_primary))
            }
            else -> {
                binding.buttonTL.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                binding.buttonEuro.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                binding.buttonSterlin.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                binding.buttonDolar.setTextColor(getResources().getColor(R.color.design_default_color_primary))
                binding.buttonOriginal.setTextColor(getResources().getColor(R.color.design_default_color_error))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}