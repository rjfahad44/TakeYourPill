package com.ft.ltd.takeyourpill.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import javax.inject.Inject

class Prefs @Inject constructor(val context: Context)
{
    private val APP_PREF_STORE = "APP_PREF_STORE"

    private val preferences: SharedPreferences = context.getSharedPreferences(APP_PREF_STORE ,Context.MODE_PRIVATE)

    var isThemeNight: Int
        get() = preferences.getInt(::isThemeNight.name, AppCompatDelegate.MODE_NIGHT_NO)
        set(value) = preferences.edit().putInt(::isThemeNight.name, value).apply()

    var isAdsActive: Boolean
        get() = preferences.getBoolean(::isAdsActive.name, true)
        set(value) = preferences.edit().putBoolean(::isAdsActive.name, value).apply()

    var alertStyle: Boolean
        get() = preferences.getBoolean(::alertStyle.name, true)
        set(value) = preferences.edit().putBoolean(::alertStyle.name, value).apply()

    var remindAgain: Boolean
        get() = preferences.getBoolean(::remindAgain.name, true)
        set(value) = preferences.edit().putBoolean(::remindAgain.name, value).apply()

    var firstRun: Boolean
        get() = preferences.getBoolean(::firstRun.name, true)
        set(value) = preferences.edit().putBoolean(::firstRun.name, value).apply()

    var buttonDelay: Int
        get() = preferences.getInt(::buttonDelay.name, 30)
        set(value) = preferences.edit().putInt(::buttonDelay.name, value).apply()

    var remindAgainAfter: Int
        get() = preferences.getInt(::remindAgainAfter.name, 10)
        set(value) = preferences.edit().putInt(::remindAgainAfter.name, value).apply()

    var theme: String
        get() = preferences.getString(::theme.name, "0")?:"0"
        set(value) = preferences.edit().putString(::theme.name, value).apply()
}