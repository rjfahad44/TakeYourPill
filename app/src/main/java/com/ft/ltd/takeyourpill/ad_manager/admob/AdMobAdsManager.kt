package com.ft.ltd.takeyourpill.ad_manager.admob

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageButton
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.ft.ltd.takeyourpill.R
import com.ft.ltd.takeyourpill.activity.MainActivity
import com.ft.ltd.takeyourpill.ad_manager.adds_view.NativeTemplateStyle
import com.ft.ltd.takeyourpill.ad_manager.adds_view.TemplateView
import com.ft.ltd.takeyourpill.ad_manager.AppAdsConfig
import com.ft.ltd.takeyourpill.ad_manager.AppAdsManager
import com.ft.ltd.takeyourpill.utils.defaultNativeTemplateStyle
import com.ft.ltd.takeyourpill.utils.exH
import java.lang.ref.WeakReference


private const val TAG = "AdMobAdsManager"

class AdMobAdsManager(val context: Context) : AppAdsManager(context, AppAdsConfig.AdMob(context)) {

    val adRequest: AdRequest
        get() = AdRequest.Builder().build()

    private var isAdLoading: Boolean = false
    private var nativeAd: NativeAd? = null
    private var interstitialAd: InterstitialAd? = null
    init {
        MobileAds.initialize(context) { _ -> }
        addTestDeviceId()
    }

    private fun addTestDeviceId() {
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(
            listOf(
                "EFBAB2C032E3ED22C86FEB7FC6B1E67E",  // MI NOTE 10 PRO TONMOY
                "1CC653D96A85DAEFAB6D9CA25C925B88",  // OPPO OFFICE
                "16BA09A6038CD5475886D3F43E381347",  // SAMSUNG OFFICE
                "356E8DFBFEAD4387A4884FC92F43F904",  // MUSFIQ VAI
                "504A6078780FA3167F9A2E32EEB934E8",  // VIVO SUMI APU
                "B497F08640F196E09D98120674AF525F",  // NOKIA TEST DEVICE
                "11AD95B86DAA27616E55E22BE16BF0F1",  // SAMSUNG TEST DEVICE
            )
        ).build()
        MobileAds.setRequestConfiguration(configuration)
    }

    fun showNativePopupAd(nativeAd: NativeAd) {
        exH {
            isAdLoading = true
            val nativeAdDialog = Dialog(context)
            nativeAdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            nativeAdDialog.setContentView(R.layout.native_popup_ad)
            val closeButton = nativeAdDialog.findViewById<ImageButton>(R.id.close)
            val templateView = nativeAdDialog.findViewById<TemplateView>(R.id.adsTemplateNative)
            templateView.setNativeAd(nativeAd)
            closeButton.setOnClickListener {
                nativeAdDialog.dismiss()
                isAdLoading = false
            }
            nativeAdDialog.setOnCancelListener {
                isAdLoading = false
            }
            exH { nativeAdDialog.show() }
            Log.i(TAG, "showPopupAd")
        }
    }

    fun preLoadAds() {
        preLoadInterstitial()
    }

    private fun preLoadInterstitial() {
        Log.i(TAG, "preLoadInterstitial: ")
        if (prefs.isAdsActive) {
            if (!isAdLoading && this.interstitialAd == null) {
                isAdLoading = true
                InterstitialAd.load(
                    context,
                    adsConfig.adUnits.interstitialAd ?: "",
                    adRequest,
                    InterstitialAdCallback()
                )
            }
        }
    }

    fun showInterstitialAd(context: Context) {
        Log.i(TAG, "displayInterstitialAd: ")
        if (interstitialAd == null) {
            preLoadInterstitial()
            return
        }

        interstitialAd?.show(context as Activity)
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                isAdLoading = false
                this@AdMobAdsManager.interstitialAd = null
                preLoadInterstitial()
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                isAdLoading = false
                this@AdMobAdsManager.interstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                this@AdMobAdsManager.interstitialAd = null
                Log.i(TAG, "InterstitialAdCallback: ${p0.message}")
                isAdLoading = false
            }
        }
    }

    fun showRewardedAd(onLoadRewardAd: (rewardedAd: RewardedAd) -> Unit) {
        if (prefs.isAdsActive) {
            RewardedAd.load(
                context,
                adsConfig.adUnits.rewardedAd ?: "",
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        rewardedAd.show(context as Activity) {}
                        onLoadRewardAd.invoke(rewardedAd)
                    }
                })
        }
    }

    fun defaultNativeTemplateStyle(): NativeTemplateStyle? {
        return context.defaultNativeTemplateStyle()
    }

    fun showNativeAds(onLoadedNativeAd : (nativeAd: NativeAd) -> Unit) {
        if (nativeAd != null && prefs.isAdsActive) {
            Log.d(TAG, "NativeAds Preloaded")
            onLoadedNativeAd.invoke(this.nativeAd!!)
        } else {
            val adLoader: AdLoader = AdLoader.Builder(
                context, adsConfig.adUnits.nativeAd ?: ""
            ).forNativeAd { nativeAd ->
                    this.nativeAd = nativeAd
                onLoadedNativeAd.invoke(nativeAd)
                Log.d(TAG, "NativeAds Loaded")
                }.build()
            adLoader.loadAd(adRequest)
        }
    }

    fun showNativeVideoAds(onLoadedVideoAd: (nativeAd: NativeAd) -> Unit) {
        if (prefs.isAdsActive) {
            val videoOptions = VideoOptions.Builder()
                .setStartMuted(true)
                .build()

            val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build()

            val adLoader = AdLoader.Builder(context, adsConfig.adUnits.nativeVideoAd ?: "")
                .forNativeAd { nativeAd ->
                    onLoadedVideoAd(nativeAd)
                }
                .withNativeAdOptions(adOptions)
                .withAdListener(AdCallBackListener())
                .build()
            adLoader.loadAd(adRequest)
            Log.d(TAG, "loadNativeVideoAds")
        }
    }

    private var bannerAd: AdView? = null
    fun showBannerAd(adViewRef: WeakReference<AdView>) {
        bannerAd?.destroy()
        bannerAd = null
        bannerAd = adViewRef.get()
        bannerAd?.visibility = View.GONE

        if(prefs.isAdsActive) {
            bannerAd?.adListener = object :AdListener(){
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.d(TAG, "BannerAds Loaded")
                    bannerAd?.visibility = View.VISIBLE
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    Log.d(TAG, "BannerAds Faild")
                    bannerAd?.visibility = View.GONE
                }
            }
            bannerAd?.visibility = View.VISIBLE
            bannerAd?.loadAd(adRequest)
        }
    }

    fun destroy() {
        isAdLoading = false
        this.interstitialAd = null
        fragmentDestroy()
    }


    fun fragmentDestroy() {
        this.nativeAd?.destroy()
        this.nativeAd = null
        exH { bannerAd?.visibility = View.GONE }
        exH { bannerAd?.destroy() }
        bannerAd = null
    }

    inner class AdCallBackListener : AdListener() {
        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            super.onAdFailedToLoad(loadAdError)
            Log.i(TAG, "a: ${loadAdError.message}")
        }
    }

    inner class InterstitialAdCallback : InterstitialAdLoadCallback() {
        override fun onAdLoaded(interstitialAd: InterstitialAd) {
            super.onAdLoaded(interstitialAd)
            this@AdMobAdsManager.interstitialAd = interstitialAd
            Log.i(TAG, "onAdLoaded onAdLoaded: interstitialAd => " + interstitialAd)
            isAdLoading = false
            this@AdMobAdsManager.interstitialAd?.show(context as Activity)
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            super.onAdFailedToLoad(loadAdError)
            this@AdMobAdsManager.interstitialAd = null
            Log.i(TAG, "onAdFailedToLoad InterstitialAdCallback: ${loadAdError.message}")
            isAdLoading = false
        }
    }
}
