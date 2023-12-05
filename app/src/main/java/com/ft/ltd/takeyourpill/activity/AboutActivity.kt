package com.ft.ltd.takeyourpill.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.ft.ltd.takeyourpill.BuildConfig
import com.ft.ltd.takeyourpill.R
import com.ft.ltd.takeyourpill.databinding.ActivityAboutBinding
import dagger.hilt.android.AndroidEntryPoint
import com.ft.ltd.takeyourpill.ad_manager.admob.AdMobAdsManager
import com.ft.ltd.takeyourpill.utils.defaultNativeTemplateStyle
import com.ft.ltd.takeyourpill.utils.onClick
import com.ft.ltd.takeyourpill.utils.viewBinding
import com.ft.ltd.takeyourpill.viewmodel.MainViewModel
import java.lang.ref.WeakReference


@AndroidEntryPoint
class AboutActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityAboutBinding::inflate)
    private val mainModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        mainModel.adMobAdsManager = AdMobAdsManager(this)
//        val adViewWeakReference = WeakReference(binding.googleAdsView)
//        mainModel.adMobAdsManager.showBannerAd(adViewWeakReference)

//        val nativeAdsTemplate = binding.adsTemplate
//        nativeAdsTemplate.setStyles(defaultNativeTemplateStyle())
//        mainModel.adMobAdsManager.showNativeAds {
//            nativeAdsTemplate.setNativeAd(it)
//            nativeAdsTemplate.isVisible = true
//        }

        binding.backBtn.onClick { finish() }
        binding.textVersion.text = getString(R.string.version, BuildConfig.VERSION_NAME)
    }

    private fun openUrl(url: String) {
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )
        startActivity(browserIntent)
    }
}