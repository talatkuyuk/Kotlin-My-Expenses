package com.talatkuyuk.myexpenses.screens.onboarding

import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.edit
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.example.onboarding.ViewPagerAdapter
import com.talatkuyuk.myexpenses.R
import com.talatkuyuk.myexpenses.databinding.FragmentOnboardingBinding
import com.talatkuyuk.myexpenses.utils.Utils


class OnboardingFragment : Fragment() {

    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var viewPager: ViewPager2

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    private val displayMetrics: DisplayMetrics by lazy { Resources.getSystem().displayMetrics }
    val screenRectPx: Rect
        get() = displayMetrics.run { Rect(0, 0, widthPixels, heightPixels) }

    private lateinit var sharedPreferences: SharedPreferences


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
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
        //return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPagerAdapter = ViewPagerAdapter(getOnboardingItems())
        viewPager = view.findViewById<ViewPager2>(R.id.onboardingViewPager)
        viewPager.adapter = viewPagerAdapter

        //val indicator: CircleIndicator3 = view.findViewById<CircleIndicator3>(R.id.indicator);
        binding.indicator.setViewPager(viewPager);

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) { // is first position
                    binding.backbtn.visibility = View.GONE
                }
                else if (position == viewPagerAdapter.itemCount - 1) { // is last position
                    binding.nextbtn.setText(R.string.finish)
                }
                else {
                    binding.backbtn.visibility = View.VISIBLE
                    binding.nextbtn.setText(R.string.next)
                }
            }
        })

        binding.skip.setOnClickListener {
            navigateToNextScreen()
        }

        binding.checkboxOnboarding.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("isVisitedOnbording", isChecked).apply();
        }

        binding.nextbtn.setOnClickListener {
            Log.d("CURRENT VIEWPAGER", viewPager.currentItem.toString())
            if (viewPager.currentItem < viewPagerAdapter.itemCount - 1) {
                fakeSwap("next")
                //viewPager.setCurrentItem(viewPager.currentItem + 1)
            } else {
                navigateToNextScreen()
            }
        }

        binding.backbtn.setOnClickListener {
            Log.d("CURRENT VIEWPAGER", viewPager.currentItem.toString())
            if (viewPager.currentItem > 0) {
                fakeSwap("back")
                //viewPager.setCurrentItem(viewPager.currentItem - 1)
            }
        }
    }

    private fun fakeSwap(direction: String) {
        // direction "back" or "next"
        val widthPx = screenRectPx.width()
        val swapoffset = widthPx.toFloat() * 2 / 3
        val factor = when(direction == "back") {
            true -> 1
            false -> -1
        }

        viewPager.apply {
            beginFakeDrag()
            fakeDragBy(factor * swapoffset)
            endFakeDrag()
        }
    }

    private fun navigateToNextScreen() {

        val name: String? = sharedPreferences.getString("name", "@")
        val gender: String? = sharedPreferences.getString("gender", "@")

        if (name == null || name == "@"  || name.length < 2 || gender == null || gender == "@" ) {
            val action = OnboardingFragmentDirections.actionOnboardingFragmentToSettingsFragment(true)
            binding.root.findNavController().navigate(action)
        } else {
            val action = OnboardingFragmentDirections.actionOnboardingFragmentToMainFragment()
            binding.root.findNavController().navigate(action)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getOnboardingItems(): MutableList<OnboardingItem> {
        val onboardingItemList = mutableListOf<OnboardingItem>()

        onboardingItemList.addAll(listOf(
            OnboardingItem(
                onboardingImage = R.drawable.image1,
                onboardingTitle = R.string.onboarding_title_1,
                onboardingDescription = R.string.onboarding_desc_1,
            ),
            OnboardingItem(
                onboardingImage = R.drawable.image2,
                onboardingTitle = R.string.onboarding_title_2,
                onboardingDescription = R.string.onboarding_desc_2,
            ),
            OnboardingItem(
                onboardingImage = R.drawable.image3,
                onboardingTitle = R.string.onboarding_title_3,
                onboardingDescription = R.string.onboarding_desc_3,
            ),
            OnboardingItem(
                onboardingImage = R.drawable.image4,
                onboardingTitle = R.string.onboarding_title_4,
                onboardingDescription = R.string.onboarding_desc_4,
            )
        ))

        return onboardingItemList
    }

}