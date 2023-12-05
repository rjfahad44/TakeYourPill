package com.ft.ltd.takeyourpill.utils

import androidx.appcompat.app.AppCompatDelegate

class Utils {
    companion object {
        fun setTheme(theme: String) {
            val appTheme = when (theme) {
                "1" ->{ AppCompatDelegate.MODE_NIGHT_NO }
                "2" -> { AppCompatDelegate.MODE_NIGHT_YES }
                else -> { AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM }
            }
            AppCompatDelegate.setDefaultNightMode(appTheme)
        }
    }
}