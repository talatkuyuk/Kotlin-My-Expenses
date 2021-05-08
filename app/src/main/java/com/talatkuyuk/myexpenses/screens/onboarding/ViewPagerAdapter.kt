package com.example.onboarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView

import com.talatkuyuk.myexpenses.R
import com.talatkuyuk.myexpenses.screens.onboarding.OnboardingItem


class ViewPagerAdapter( private val onboardingItems: List<OnboardingItem>)
    : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.onboarding_slider_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(onboardingItems[position])
    }

    override fun getItemCount(): Int = onboardingItems.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val onboardingImage = view.findViewById<ImageView>(R.id.onboardingImage);
        private val onboardingTitle = view.findViewById<TextView>(R.id.onboardingTitle);
        private val onboardingtDescription = view.findViewById<TextView>(R.id.onboardingtDescription);

        fun bind(onboardingItem: OnboardingItem) {
            onboardingImage.setImageResource(onboardingItem.onboardingImage)
            onboardingTitle.setText(onboardingItem.onboardingTitle)
            onboardingtDescription.setText(onboardingItem.onboardingDescription)
        }
    }
}