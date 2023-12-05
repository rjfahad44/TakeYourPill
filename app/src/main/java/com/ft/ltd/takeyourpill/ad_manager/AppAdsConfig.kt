package com.ft.ltd.takeyourpill.ad_manager

import android.content.Context
import com.ft.ltd.takeyourpill.BuildConfig
import com.ft.ltd.takeyourpill.R

sealed class AppAdsConfig(private val context: Context, val adUnits:AdUnits,val isActive:Boolean = false) {
    class AdMob(context: Context): AppAdsConfig(context,AdMobAdUnit(context), BuildConfig.DEBUG)
    class Facebook(context: Context):AppAdsConfig(context,FacebookAdUnits(context))
    class ApplovinMax(context: Context):AppAdsConfig(context,ApplovinMaxAdUnits(context))

    companion object{
       @JvmStatic
       fun configs(context: Context):List<AppAdsConfig>{
           return AppAdsConfig::class.nestedClasses
               .filter { it.nestedClasses.contains(AppAdsConfig::class) }
               .map { it.constructors.first().call(context) as AppAdsConfig }
       }
    }
}

class AdMobAdUnit( context: Context) :AdUnits {
    override val bannerAd: String =context.resources.getString(R.string.admob_banner_ads_id)
    override val interstitialAd: String =context.resources.getString(R.string.admob_interstitial_ads_id)
    override val nativeAd: String = context.resources.getString(R.string.admob_native_ads_id)
    override val nativeVideoAd: String =context.resources.getString(R.string.admob_video_ads_id)
    override val appOpenAd: String =context.resources.getString(R.string.admob_open_ads_id)
    override val rewardedAd: String = context.resources.getString(R.string.admob_reward_ads_id)
}
class FacebookAdUnits( context: Context):AdUnits{
    override val bannerAd: String = context.resources.getString(R.string.facebook_banner_ads_id)
    override val interstitialAd: String = context.resources.getString(R.string.facebook_interstitial_ads_id)
    override val nativeAd: String =context.resources.getString(R.string.facebook_video_native_ads_id)
    override val nativeVideoAd: String = context.resources.getString(R.string.facebook_native_ads_id)
    override val appOpenAd: String? = null
    override val rewardedAd: String? = null
}
class ApplovinMaxAdUnits(context: Context):AdUnits{
    override val bannerAd: String = context.resources.getString(R.string.max_banner)
    override val interstitialAd: String = context.resources.getString(R.string.max_interstitial)
    override val nativeAd: String = context.resources.getString(R.string.max_native)
    override val nativeVideoAd: String? = null
    override val appOpenAd: String? = null
    override val rewardedAd: String? = null
}

interface AdUnits{
    val bannerAd:String?
    val interstitialAd:String?
    val nativeAd:String?
    val nativeVideoAd:String?
    val appOpenAd:String?
    val rewardedAd:String?
}