package com.ft.ltd.takeyourpill.ad_manager.facebook

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.AdListener
import com.facebook.ads.AdOptionsView
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import com.facebook.ads.MediaView
import com.facebook.ads.NativeAd
import com.facebook.ads.NativeAdLayout
import com.facebook.ads.NativeAdListener
import com.ft.ltd.takeyourpill.R
import com.ft.ltd.takeyourpill.ad_manager.AppAdsConfig
import com.ft.ltd.takeyourpill.ad_manager.AppAdsManager
import com.ft.ltd.takeyourpill.utils.exH
import java.lang.ref.WeakReference


private const val TAG = "FacebookAdsManager"
class FacebookAdsManager(private val context: Context):
    AppAdsManager(context, AppAdsConfig.Facebook(context)) {

    private  var adView: AdView?=null
    private var interstitialAd: InterstitialAd?=null


    fun bannerAds(): AdView {
       val adsId = adsConfig.adUnits.bannerAd
         adView = AdView(
            context,
             adsId,
            AdSize.BANNER_HEIGHT_50
        )
        return adView as AdView
    }
    fun addBannerOnLinearLayout(layoutRef: WeakReference<LinearLayout>){
        try {

            val layout = layoutRef.get()
            if (!prefs.isAdsActive && layout!=null) {

                if (adView == null) {
                    adView = bannerAds()
                }
                layout.removeAllViews()
                layout.addView(adView)


                val adListener: AdListener = object : AdListener {
                    override fun onError(p0: Ad?, p1: AdError?) {

                    }

                    override fun onAdLoaded(p0: Ad?) {

                    }

                    override fun onAdClicked(p0: Ad?) {

                    }

                    override fun onLoggingImpression(p0: Ad?) {

                    }

                }
                adView?.loadAd(adView?.buildLoadAdConfig()?.withAdListener(adListener)?.build())
            }
        }catch (e:Exception){e.printStackTrace()}

    }
    fun showInterstitialAd(){
        if(!isInterstitialAdsAlreadyLoading && !prefs.isAdsActive) {

            isInterstitialAdsAlreadyLoading = true
            interstitialAd = InterstitialAd(
                context,
               adsConfig.adUnits.interstitialAd
            )
            interstitialAd?.let {
                it.loadAd(
                    it
                        .buildLoadAdConfig()
                        .withAdListener(object : InterstitialAdListener {
                            override fun onError(p0: Ad?, p1: AdError?) {
                                isInterstitialAdsAlreadyLoading = false
                            }

                            override fun onAdLoaded(p0: Ad?) {
                                it.show()
                            }

                            override fun onAdClicked(p0: Ad?) {
                                isInterstitialAdsAlreadyLoading = false
                            }

                            override fun onLoggingImpression(p0: Ad?) {

                            }

                            override fun onInterstitialDisplayed(p0: Ad?) {
                            }

                            override fun onInterstitialDismissed(p0: Ad?) {
                                isInterstitialAdsAlreadyLoading = false
                            }

                        })
                        .build()
                )
            }
        }

    }


    public fun loadNativeAds(nativeAdLayoutRef:WeakReference<NativeAdLayout>, isVideo:Boolean = false, adsComplete:()->Unit){

        val nativeAdLayout:NativeAdLayout? = nativeAdLayoutRef.get();
        if(!prefs.isAdsActive && nativeAdLayout !=null) {

            val nativeAdsId = if (isVideo) {
                adsConfig.adUnits.nativeAd
            } else {
               adsConfig.adUnits.nativeVideoAd
            }
            val nativeAd: NativeAd = NativeAd(nativeAdLayout.context, nativeAdsId)
            nativeAd.loadAd(
                nativeAd.buildLoadAdConfig()
                    .withAdListener(object : NativeAdListener {
                        override fun onError(p0: Ad?, p1: AdError?) {

                        }

                        override fun onAdLoaded(p0: Ad?) {
                            if (nativeAd == null || nativeAd != p0) {
                                return
                            }
                            // Inflate Native Ad into Container
                            inflateAd(nativeAd,nativeAdLayoutRef)

                            exH { adsComplete() }
                        }

                        override fun onAdClicked(p0: Ad?) {

                        }

                        override fun onLoggingImpression(p0: Ad?) {

                        }

                        override fun onMediaDownloaded(p0: Ad?) {
                        }

                    })
                    .build()
            )
        }
    }
    public fun loadNativeAds(isVideo:Boolean = false,adsComplete:(NativeAd)->Unit){

        if(!prefs.isAdsActive) {

            val nativeAdsId = if (isVideo) {
                adsConfig.adUnits.nativeAd
            } else {
                adsConfig.adUnits.nativeVideoAd
            }
            val nativeAd: NativeAd = NativeAd(context, nativeAdsId)
            nativeAd.loadAd(
                nativeAd.buildLoadAdConfig()
                    .withAdListener(object : NativeAdListener {
                        override fun onError(p0: Ad?, p1: AdError?) {

                        }

                        override fun onAdLoaded(p0: Ad?) {
                            if (nativeAd == null || nativeAd != p0) {
                                return
                            }

                            exH { adsComplete(nativeAd) }
                        }

                        override fun onAdClicked(p0: Ad?) {

                        }

                        override fun onLoggingImpression(p0: Ad?) {

                        }

                        override fun onMediaDownloaded(p0: Ad?) {
                        }

                    })
                    .build()
            )
        }
    }


    fun destroy(){
        exH { adView?.destroy() }
        exH {
            if (interstitialAd != null) {
                interstitialAd?.destroy()
            }
        }
        exH {
            System.gc()
        }
        interstitialAdsCount = 1
    }

    fun interstitialWithCount(isPlaying: Boolean) {

        if (!prefs.isAdsActive) {
            if ((interstitialAdsCount == 1) || (interstitialAdsCount % 2 == 0)) {
                showInterstitialAd()
            }
            interstitialAdsCount++
        }

    }
    companion object{
        private var interstitialAdsCount:Int = 1
        private var isInterstitialAdsAlreadyLoading = false

        public fun inflateAd(nativeAd: NativeAd,nativeAdLayoutRef:WeakReference<NativeAdLayout> ) {

            val  nativeAdLayout = nativeAdLayoutRef.get()

            if(nativeAdLayout!=null) {
                nativeAdLayout.removeAllViews()
                nativeAd.unregisterView()
                val inflater = LayoutInflater.from(nativeAdLayout.context)
                val fbNativeAdsLayout = inflater?.inflate(
                    R.layout.facebook_native_layout,
                    nativeAdLayout,
                    false
                ) as LinearLayout
                nativeAdLayout.addView(fbNativeAdsLayout)
                val adChoicesContainer: LinearLayout =
                    fbNativeAdsLayout.findViewById(R.id.adChoicesContainer)
                val adOptionsView = AdOptionsView(nativeAdLayout.context, nativeAd, nativeAdLayout)
                adChoicesContainer.removeAllViews()
                adChoicesContainer.addView(adOptionsView, 0)
                val nativeAdIcon: MediaView = fbNativeAdsLayout.findViewById(R.id.nativeAdIcon)
                val nativeAdTitle = fbNativeAdsLayout.findViewById<TextView>(R.id.nativeAdTitle)
                val nativeAdMedia: MediaView = fbNativeAdsLayout.findViewById(R.id.nativeAdMedia)
                val nativeAdSocialContext =
                    fbNativeAdsLayout.findViewById<TextView>(R.id.nativeAdSocialContext)
                val nativeAdBody = fbNativeAdsLayout.findViewById<TextView>(R.id.nativeAdBody)
                val sponsoredLabel =
                    fbNativeAdsLayout.findViewById<TextView>(R.id.nativeAdSponsoredLabel)
                val nativeAdCallToAction: Button =
                    fbNativeAdsLayout.findViewById(R.id.nativeAdCallToAction)
                nativeAdTitle.text = nativeAd.advertiserName
                nativeAdBody.text = nativeAd.adBodyText
                nativeAdSocialContext.text = nativeAd.adSocialContext
                nativeAdCallToAction.visibility =
                    if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
                nativeAdCallToAction.text = nativeAd.adCallToAction
                sponsoredLabel.text = nativeAd.sponsoredTranslation
                val clickableViews: MutableList<View> = ArrayList()
                clickableViews.add(nativeAdTitle)
                clickableViews.add(nativeAdCallToAction)
                nativeAd.registerViewForInteraction(
                    fbNativeAdsLayout, nativeAdMedia, nativeAdIcon, clickableViews
                )
            }
        }
    }
}