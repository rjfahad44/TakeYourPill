package com.ft.ltd.takeyourpill.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.OVER_SCROLL_NEVER
import androidx.fragment.app.viewModels
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ft.ltd.takeyourpill.BuildConfig
import dagger.hilt.android.AndroidEntryPoint
import com.ft.ltd.takeyourpill.R
import com.ft.ltd.takeyourpill.activity.AboutActivity
import com.ft.ltd.takeyourpill.activity.WebActivity
import com.ft.ltd.takeyourpill.activity.WebActivity.Companion.IS_HTML_TEXT
import com.ft.ltd.takeyourpill.activity.WebActivity.Companion.TITLE
import com.ft.ltd.takeyourpill.activity.WebActivity.Companion.URL
import com.ft.ltd.takeyourpill.activity.WebActivity.Companion.privacyPoliciesHtmlText
import com.ft.ltd.takeyourpill.utils.Prefs
import com.ft.ltd.takeyourpill.utils.Utils
import com.ft.ltd.takeyourpill.viewmodel.PreferencesViewModel
import javax.inject.Inject

@AndroidEntryPoint
class PreferencesFragment : PreferenceFragmentCompat() {

    private val model: PreferencesViewModel by viewModels()

    @Inject
    lateinit var prefs: Prefs

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.apply {
            clipToPadding = false
            overScrollMode = OVER_SCROLL_NEVER
            setPadding(
                0,
                0,
                0,
                view.context.resources.getDimension(R.dimen.list_with_navigation_padding).toInt()
            )
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // If we are running on older than oreo, set summary to represent the available options in android settings
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            findPreference<Preference>("notificationOptions")?.summary =
                getString(R.string.settings_notification_options_summary_legacy)
        }
        findPreference<Preference>("notificationOptions")?.setOnPreferenceClickListener {
            val intent = Intent()
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"

            intent.putExtra("app_package", requireContext().packageName)
            intent.putExtra("app_uid", requireContext().applicationInfo.uid)
            intent.putExtra("android.provider.extra.APP_PACKAGE", requireContext().packageName)

            startActivity(intent)
            true
        }

        findPreference<Preference>("showAbout")?.setOnPreferenceClickListener {
            val intent = Intent(requireActivity(), AboutActivity::class.java)
            startActivity(intent)
            true
        }

        findPreference<Preference>("privacyPolicy")?.setOnPreferenceClickListener {
            val intent = Intent(requireActivity(), WebActivity::class.java)
            intent.putExtra(TITLE, requireContext().getString(R.string.privacy_policy))
            intent.putExtra(URL, privacyPoliciesHtmlText)
            intent.putExtra(IS_HTML_TEXT,true)
            startActivity(intent)
            true
        }

        findPreference<Preference>("addTestData")?.isVisible = BuildConfig.DEBUG
        findPreference<Preference>("addTestData")?.setOnPreferenceClickListener {
            model.addTestData(requireContext())
            true
        }

        findPreference<ListPreference>("themeKey")?.setOnPreferenceChangeListener { _, newValue ->
            prefs.theme = newValue.toString() // Doesn't save?
            Utils.setTheme(prefs.theme)
            true
        }

    }
}