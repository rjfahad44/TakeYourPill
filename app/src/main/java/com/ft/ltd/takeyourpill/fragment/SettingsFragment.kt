package com.ft.ltd.takeyourpill.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.transition.MaterialFadeThrough
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import com.ft.ltd.takeyourpill.R
import com.ft.ltd.takeyourpill.databinding.FragmentSettingsBinding
import com.ft.ltd.takeyourpill.viewmodel.MainViewModel

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val mainModel: MainViewModel by activityViewModels()
    private val binding by viewBinding(FragmentSettingsBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        mainModel.shouldScrollUp.observe(viewLifecycleOwner) {
            binding.settingsScrollView.smoothScrollTo(0, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        //mainModel.adMobAdsManager.showInterstitialAd(requireActivity())
    }

}