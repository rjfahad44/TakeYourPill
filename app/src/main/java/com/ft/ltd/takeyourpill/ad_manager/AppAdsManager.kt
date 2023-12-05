package com.ft.ltd.takeyourpill.ad_manager

import android.content.Context
import com.ft.ltd.takeyourpill.utils.Prefs
import javax.inject.Inject

open class AppAdsManager(private val context: Context, val adsConfig:AppAdsConfig) {
    init { checkConfig() }
    val isActive:Boolean = adsConfig.isActive

    @Inject
    lateinit var prefs: Prefs
    private fun checkConfig() {
        prefs = Prefs(context)
        if (prefs.isAdsActive){
            val count = AppAdsConfig.configs(context).count { it.isActive }
            if(count>1){
                throw Exception("You can't active multiple ad at a time (active ad count $count)")
            }
        }
    }
}