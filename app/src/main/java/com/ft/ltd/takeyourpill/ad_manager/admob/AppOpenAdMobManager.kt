package com.ft.ltd.takeyourpill.ad_manager.admob

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.ft.ltd.takeyourpill.ad_manager.AppAdsConfig
import com.ft.ltd.takeyourpill.utils.Prefs
import java.util.*

private const val TAG = "AppOpenAdMobManager"
class AppOpenAdMobManager (private var myApplication: Application) : LifecycleObserver,Application.ActivityLifecycleCallbacks {

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false
    private var loadTime: Long = 0
    private var currentActivity: Activity? = null
    private var prefs: Prefs

    init {
        this.myApplication.registerActivityLifecycleCallbacks(this)
        MobileAds.initialize(this.myApplication) {}
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        prefs = Prefs(this.myApplication)
    }

    fun showOpenAds() {
        if(prefs.isAdsActive){
            Log.d(TAG, "showOpenAds Called")
           showAdIfAvailable()
        }
    }

    fun loadAd(context: Context) {
        if (isLoadingAd || isAdAvailable()) {
            return
        }

        isLoadingAd = true
        val request = AdRequest.Builder().build()
        Log.d(TAG, "OpenAds id : ${AppAdsConfig.AdMob(myApplication).adUnits.appOpenAd?:""}")
        AppOpenAd.load(context, AppAdsConfig.AdMob(myApplication).adUnits.appOpenAd?:"", request,
            object : AppOpenAd.AppOpenAdLoadCallback() {

                override fun onAdLoaded(ad: AppOpenAd) {
                    Log.e(TAG, "Ad was loaded.")
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.d(TAG, "onAdFailedToLoad : ${loadAdError.message}")
                    isLoadingAd = false
                }
            })
    }

    private fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd) {
            Log.d(TAG, "The app open ad is already showing.")
            return
        }

        // If the app open ad is not available yet, invoke the callback then load the ad.
        if (!isAdAvailable()) {
            Log.d(TAG, "The app open ad is not ready yet.")
            onShowAdCompleteListener.onShowAdComplete()
            loadAd(activity)
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

            override fun onAdDismissedFullScreenContent() {
                // Called when full screen content is dismissed.
                // Set the reference to null so isAdAvailable() returns false.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                appOpenAd = null
                isShowingAd = false

                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when fullscreen content failed to show.
                // Set the reference to null so isAdAvailable() returns false.
                Log.d(TAG, "onAdFailedToShowFullScreenContent : ${adError.message}")
                appOpenAd = null
                isShowingAd = false

                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity)
            }

            override fun onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }
        isShowingAd = true
        appOpenAd?.show(activity)
    }

    private fun showAdIfAvailable() {
        Log.d(TAG, "showAdIfAvailable Called")
        currentActivity?.let {
            Log.d(TAG, "currentActivity found")
            showAdIfAvailable(it,
                object : OnShowAdCompleteListener {
                    override fun onShowAdComplete() {
                        Log.d(TAG, "Open Ads closed")
                        // Empty because the user will go back to the activity that shows the ad.
                    }
                })
        }
    }


    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }
    fun onMoveToForeground() {
        // Show the ad (if available) when the app moves to foreground.
        currentActivity?.let {
            showAdIfAvailable()
        }
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {}

    override fun onActivityStarted(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityResumed(p0: Activity) {
        currentActivity = p0
    }
    override fun onActivityPaused(p0: Activity) {}
    override fun onActivityStopped(p0: Activity) {}
    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}
    override fun onActivityDestroyed(p0: Activity) {
        currentActivity = null
    }

    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }
    
}