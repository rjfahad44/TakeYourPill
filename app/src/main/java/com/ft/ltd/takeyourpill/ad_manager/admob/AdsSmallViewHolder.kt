package com.ft.ltd.takeyourpill.ad_manager.admob

import androidx.recyclerview.widget.RecyclerView
import com.ft.ltd.takeyourpill.databinding.SmallSizeAdLayoutBinding
import com.google.android.gms.ads.nativead.NativeAd

class AdsSmallViewHolder(private val binding: SmallSizeAdLayoutBinding): RecyclerView.ViewHolder(binding.root) {
    fun bindAd(nativeAd: NativeAd){
        binding.adsTemplate.setNativeAd(nativeAd)
    }
}