package com.talatkuyuk.myexpenses.screens.splash

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.talatkuyuk.myexpenses.R
import com.talatkuyuk.myexpenses.data.repository.PreferenceRepository
import com.talatkuyuk.myexpenses.screens.onboarding.OnboardingFragmentDirections


class SplashFragment : Fragment() {

    private val preferenceRepository by lazy { PreferenceRepository(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed(runneable, 2000)

    }

    override fun onPause() {
        super.onPause()
        Handler(Looper.getMainLooper()).removeCallbacks(runneable)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Handler(Looper.getMainLooper()).removeCallbacks(runneable)
    }

    var runneable: Runnable = object : Runnable {
        override fun run() {

            if ( preferenceRepository.getIsVisitedOnboarding() ) {
                navigateToNextScreen()

            } else {
                val action = SplashFragmentDirections.actionSplashFragmentToOnboardingFragment()
                view!!.findNavController().navigate(action)
            }

            onDestroyView()
        }
    }

    private fun navigateToNextScreen() {

        val name: String = preferenceRepository.getName()
        val gender: String = preferenceRepository.getGender()

        if (name.length < 2 || gender == "" ) {
            val action = SplashFragmentDirections.actionSplashFragmentToSettingsFragment(true)
            requireView().findNavController().navigate(action)
        } else {
            val action = SplashFragmentDirections.actionSplashFragmentToMainFragment()
            requireView().findNavController().navigate(action)
        }
    }
}