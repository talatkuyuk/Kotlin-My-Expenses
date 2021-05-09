package com.talatkuyuk.myexpenses.screens.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.talatkuyuk.myexpenses.R
import com.talatkuyuk.myexpenses.data.repository.PreferenceRepository
import com.talatkuyuk.myexpenses.utils.Utils

class SettingsFragment : PreferenceFragmentCompat() {

    private val preferenceRepository by lazy { PreferenceRepository(requireContext()) }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                validatePreferences()
            }
        })
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (preference.key == context?.getString(R.string.pref_key_button)) {
            validatePreferences()
            return true
        }
        return false
    }

    private fun validatePreferences() {
        var validationErrorMessage: String = ""

        val name: String = preferenceRepository.getName()
        if ( name.length < 2) {
            validationErrorMessage = "Name must be minimum 2 characters. "
        }

        val gender: String = preferenceRepository.getGender()
        Log.d("gender: ", gender)
        if ( gender == "" || gender == null) {
            validationErrorMessage += "Gender must be choosen. "
        }

        if (validationErrorMessage == "") {

            val args: SettingsFragmentArgs by navArgs()
            if (args.attention) {
                Log.d("SETTINGS TO MAIN", "Navigate")
                val action = SettingsFragmentDirections.actionSettingsFragmentToMainFragment()
                findNavController().navigate(action)
            } else {
                Log.d("SETTINGS TO MAIN", "Pop")
                val action = SettingsFragmentDirections.actionSettingsFragmentToMainFragment()
                findNavController().popBackStack(R.id.mainFragment, false)
            }

        } else {
            Utils().showDialog(requireActivity(), validationErrorMessage)
        }
    }



    override fun onStart() {
        super.onStart()
        val args: SettingsFragmentArgs by navArgs()
        if (args.attention) {
            Toast.makeText(context,"Please complete your Identity first.", Toast.LENGTH_LONG).show()
        }
    }

}