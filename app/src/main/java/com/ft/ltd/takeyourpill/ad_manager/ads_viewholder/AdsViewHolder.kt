package com.ft.ltd.takeyourpill.ad_manager.ads_viewholder

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ft.ltd.takeyourpill.ad_manager.admob.AdMobAdsManager
import com.ft.ltd.takeyourpill.databinding.RowAdsLayoutBinding
import com.ft.ltd.takeyourpill.utils.defaultNativeTemplateStyle

class AdsViewHolder constructor(
    val context: Context,
    private val binding: RowAdsLayoutBinding,
    private val appAdsManager: AdMobAdsManager
): RecyclerView.ViewHolder(binding.root) {

    init {
        binding.adsTemplate.visibility = View.GONE
        appAdsManager.showNativeAds { nativeAd ->
            val nativeTemplateStyle = context.defaultNativeTemplateStyle()
            val template = binding.adsTemplate
            template.setStyles(nativeTemplateStyle)
            template.setNativeAd(nativeAd)
            binding.adsTemplate.visibility = View.VISIBLE
        }


      /*  applovinAdsManager.loadNativeAds(layoutRef) {
            binding.applovenAds.visibility = View.VISIBLE
            binding.removeAds.visibility = View.VISIBLE
            binding.removeAds.setOnClickListener {
                (context as MainActivity).loadSubscriptionFragmentDialog()
            }
        }*/
    }

    fun bind() = binding.run {

    }

}