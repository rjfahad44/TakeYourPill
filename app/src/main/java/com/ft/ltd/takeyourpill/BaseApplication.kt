package com.ft.ltd.takeyourpill

import android.app.Application
import com.ft.ltd.takeyourpill.ad_manager.admob.AppOpenAdMobManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BaseApplication : Application() {

    private lateinit var appOpenAdMobManager: AppOpenAdMobManager
    override fun onCreate() {
        super.onCreate()
        appOpenAdMobManager = AppOpenAdMobManager(this)
        appOpenAdMobManager.showOpenAds()
        initLogger()
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return "(${element.fileName}:${element.lineNumber})#${element.methodName}"
                }
            })
        }
    }
}