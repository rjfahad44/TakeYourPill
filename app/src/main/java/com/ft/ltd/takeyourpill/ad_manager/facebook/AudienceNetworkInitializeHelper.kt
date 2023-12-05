package com.ft.ltd.takeyourpill.ad_manager.facebook

import android.content.Context
import android.util.Log
import com.ft.ltd.takeyourpill.BuildConfig
import com.facebook.ads.AdSettings
import com.facebook.ads.AudienceNetworkAds
import com.facebook.ads.AudienceNetworkAds.InitListener
import com.facebook.ads.AudienceNetworkAds.InitResult

class AudienceNetworkInitializeHelper : InitListener {
    override fun onInitialized(result: InitResult) {
        Log.d(AudienceNetworkAds.TAG, result.message)
    }

    companion object {

        @JvmStatic
        fun initialize(context: Context?) {
            if (!AudienceNetworkAds.isInitialized(context)) {
                if (BuildConfig.DEBUG) {
                    AdSettings.turnOnSDKDebugger(context)
                }
                AudienceNetworkAds
                    .buildInitSettings(context)
                    .withInitListener(AudienceNetworkInitializeHelper())
                    .initialize()
            }
        }
    }
}