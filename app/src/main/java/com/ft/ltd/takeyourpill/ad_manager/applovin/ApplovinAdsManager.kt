package com.ft.ltd.takeyourpill.ad_manager.applovin

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.sdk.AppLovinSdk
import com.ft.ltd.takeyourpill.ad_manager.AppAdsConfig
import com.ft.ltd.takeyourpill.ad_manager.AppAdsManager
import com.ft.ltd.takeyourpill.utils.exH
import com.ft.ltd.takeyourpill.utils.toPx
import java.lang.ref.WeakReference
import java.util.*
import kotlin.concurrent.schedule

private const val TAG = "ApplovinAdsManager"
typealias NativeAdCallback = ((adview:MaxNativeAdView?) -> Unit)?
class ApplovinAdsManager(private val context: Context) :
    AppAdsManager(context, AppAdsConfig.ApplovinMax(context)), LifecycleObserver{

    private var interstitialAd: MaxInterstitialAd ? = null
    private var bannerAd:MaxAdView?=null
    private var nativeAdLoader:MaxNativeAdLoader?=null
    private var timer:Timer?=null
    var isStartTimerCounter:Boolean = false

    private fun addTestDeviceId(){
        AppLovinSdk.getInstance(context).apply {
            mediationProvider = "max"
        }.settings.apply {
            testDeviceAdvertisingIds = listOf(
                "8fbbed6b-3377-41b2-bcb0-e25f6e1c0b37",
                "9c957909-cba2-44ce-bc75-1ba6071653f8",
                "a6af7865-2873-4faa-9462-0d2074248f67",
                "29395c88-b170-4d62-abff-837d6f976a6b"
            )
            setVerboseLogging(true)
        }
    }
    fun initialize(){
        if(adsConfig.isActive) {
            addTestDeviceId()
            AppLovinSdk.initializeSdk(context) { addTestDeviceId() }
        }
    }

    fun showInterstitialAd() {

        if (!isInterstitialAdsAlreadyLoading && !prefs.isAdsActive) {
            isInterstitialAdsAlreadyLoading = true
            if(interstitialAd == null) {
                interstitialAd =
                    MaxInterstitialAd(adsConfig.adUnits.interstitialAd, context as Activity?)
            }
            interstitialAd?.setListener(WrapperInterstitialListener())
            interstitialAd?.loadAd()
            Log.i(TAG, "resumeInterstitialAd showInterstitialAd")
        }

    }

    fun interstitialWithCount(playing: Boolean) {
        if (!prefs.isAdsActive) {
            if (interstitialAdsCount == 3) {
                showInterstitialAd()
                resumeInterstitialAd()
            }
            Log.i(TAG, "interstitialWithCount: $interstitialAdsCount")
            interstitialAdsCount++
        }
    }

    public fun loadNativeAds(onAdLoadedFunc: NativeAdCallback){
        loadNativeAds(null,onAdLoadedFunc)
    }
    public fun loadNativeAds(layoutRef: WeakReference<FrameLayout>, onAdLoadedFunc: () -> Unit){
        val func: NativeAdCallback = { onAdLoadedFunc.invoke() }
        loadNativeAds(layoutRef,func)
    }
    public fun loadNativeAds(layoutRef: WeakReference<FrameLayout>?, onAdLoadedFunc: NativeAdCallback){
        exH {
            val containerLayout = layoutRef?.get()
            if (!prefs.isAdsActive) {
                nativeAdLoader = MaxNativeAdLoader(adsConfig.adUnits.nativeAd, containerLayout?.context?:context)
                nativeAdLoader?.setNativeAdListener(object : MaxNativeAdListener() {
                    override fun onNativeAdLoaded(naviewAdView: MaxNativeAdView?, ad: MaxAd?) {
                        super.onNativeAdLoaded(naviewAdView, ad)
                        exH {
                            containerLayout?.removeAllViews()
                            containerLayout?.addView(naviewAdView)
                            onAdLoadedFunc?.invoke(naviewAdView)
                        }

                    }

                    override fun onNativeAdLoadFailed(p0: String?, p1: MaxError?) {
                        super.onNativeAdLoadFailed(p0, p1)
                    }
                })
                nativeAdLoader?.loadAd()
            }
        }
    }
    fun addBannerOnLinearLayout(layoutRef: WeakReference<LinearLayout>){
        exH {
            val containerLayout = layoutRef.get()
            if (!prefs.isAdsActive && containerLayout != null) {
                bannerAd = MaxAdView(adsConfig.adUnits.bannerAd, containerLayout.context)
                val width = ViewGroup.LayoutParams.MATCH_PARENT
                val heightPx = 50.toPx
                bannerAd?.layoutParams = FrameLayout.LayoutParams(width, heightPx)
                bannerAd?.setListener(WrapperBannerAdListener {
                    containerLayout.removeAllViews()
                    containerLayout.addView(bannerAd)
                })

                bannerAd?.loadAd()
            }
        }
    }


    private fun resumeInterstitialAd(){
        if(isStartTimerCounter && !prefs.isAdsActive) {
            timer = Timer()
            val period =  90000L
            timer?.schedule(period, period) {

                if(context is LifecycleOwner &&  context.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)){
                    showInterstitialAd()
                }
                if(prefs.isAdsActive){
                    pauseInterstitialAd()
                }
            }
        }
    }
    private fun pauseInterstitialAd(){
        timer?.cancel()
        timer = null
    }
    fun destroy(){
        interstitialAdsCount = 0
    }

    inner class WrapperBannerAdListener(private val onAdLoadedFunc:()->Unit):MaxAdViewAdListener{
        override fun onAdLoaded(ad: MaxAd?) {
            onAdLoadedFunc.invoke()
            Log.i(TAG, "banner onAdLoaded: ")
        }

        override fun onAdDisplayed(ad: MaxAd?) {
            Log.i(TAG, "banner onAdDisplayed: ")
        }

        override fun onAdHidden(ad: MaxAd?) {
            Log.i(TAG, "banner onAdHidden: ")
        }

        override fun onAdClicked(ad: MaxAd?) {
            Log.i(TAG, "banner onAdClicked: ")
        }

        override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
            Log.i(TAG, "banner onAdLoadFailed: ${error?.message} ${error?.waterfall.toString()}")
        }

        override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
            Log.i(TAG, "banner onAdDisplayFailed: ${error?.message}")
        }

        override fun onAdExpanded(ad: MaxAd?) {
            Log.i(TAG, "banner onAdExpanded: ")
        }

        override fun onAdCollapsed(ad: MaxAd?) {
            Log.i(TAG, "banner onAdCollapsed: ")
        }

    }
    inner class WrapperInterstitialListener: MaxAdListener {
        override fun onAdLoaded(ad: MaxAd?) {
            if(interstitialAd?.isReady == true){
                interstitialAd?.showAd()
            }
            isInterstitialAdsAlreadyLoading = true
            Log.i(TAG, "onAdLoaded: ")
        }

        override fun onAdDisplayed(ad: MaxAd?) {
            Log.i(TAG, "onAdDisplayed: ")
            isInterstitialAdsAlreadyLoading = true
        }

        override fun onAdHidden(ad: MaxAd?) {
            Log.i(TAG, "onAdHidden: ")
            isInterstitialAdsAlreadyLoading = false
        }

        override fun onAdClicked(ad: MaxAd?) {
            Log.i(TAG, "onAdClicked: ")
        }

        override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
            Log.i(TAG, "WrapperInterstitialListener onAdLoadFailed: ${error?.message} ${error?.waterfall.toString()}")
            isInterstitialAdsAlreadyLoading = false

        }

        override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
            Log.i(TAG, "onAdDisplayFailed: ")
            isInterstitialAdsAlreadyLoading = false

        }

    }



    companion object {
        private var interstitialAdsCount: Int = 0
        private var isInterstitialAdsAlreadyLoading = false
    }

}
