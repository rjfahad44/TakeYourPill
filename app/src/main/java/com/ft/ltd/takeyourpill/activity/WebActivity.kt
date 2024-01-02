package com.ft.ltd.takeyourpill.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.ft.ltd.takeyourpill.BuildConfig
import com.ft.ltd.takeyourpill.R
import com.ft.ltd.takeyourpill.databinding.ActivityWebBinding

class WebActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.mainProgress.isVisible = true
        val title = intent.getStringExtra(TITLE)
        val urlString = intent.getStringExtra(URL)
        title?.let {
            binding.toolbarTitle.text = it
        }
        urlString?.let {
            startWebView(binding.webview, it)
        }

        binding.backBtn.setOnClickListener {
            val intent = Intent("tet")
            intent.putExtra("cat", "meow")
            setResult(RESULT_OK, intent)
            onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun startWebView(webView: WebView, urlString: String) {
        webView.isScrollContainer = false
        webView.settings.javaScriptEnabled = true
        webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        webView.settings.loadWithOverviewMode = false
        webView.settings.useWideViewPort = false
        webView.settings.builtInZoomControls = false
        webView.settings.displayZoomControls = false
        webView.settings.userAgentString = "Android"
        webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        webView.settings.domStorageEnabled = true
        webView.clearCache(true)
        webView.webChromeClient = WebChromeClient()
        if (intent.getBooleanExtra(IS_HTML_TEXT, false)){
            webView.loadData(urlString, "text/html", "UTF-8")
        }else{
            webView.loadUrl(urlString)
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                view.loadUrl(request.url.toString())
                return true
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.mainProgress.isVisible = true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                binding.mainProgress.isVisible = false
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                if (!BuildConfig.DEBUG) {
                    finish()
                }
            }
        }
    }

    override fun finish() {
        super.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {

//        if (!binding.webview.canGoBack()) {
//            setResult(Activity.RESULT_OK)
//            super.onBackPressed()
//        } else {
//            binding.webview.goBack()
//        }

        binding.webview.webViewClient = WebViewClient()
        binding.webview.webChromeClient = null
        binding.webview.stopLoading()
        binding.webview.removeAllViews()
        binding.webview.destroy()
        finish()
        super.onBackPressed()

    }

    companion object{
        const val TITLE = "TITLE"
        const val URL = "URL"
        const val IS_HTML_TEXT = "IS_HTML_TEXT"
        const val privacyPoliciesHtmlText = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>TakeYourPill App Privacy Policy</title>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "\n" +
                "    <h1>Privacy Policy for TakeYourPill App</h1>\n" +
                "\n" +
                "    <p><strong>Last updated:</strong> 02/01/2024 (dd/mm/yyyy)</p>\n" +
                "\n" +
                "    <p>This Privacy Policy explains how [Your Company Name] (\"we,\" \"us,\" or \"our\") collects, uses, shares, and protects user information obtained through the TakeYourPill mobile application (the \"App\").</p>\n" +
                "\n" +
                "    <p>By downloading, installing, or using the App, you agree to the terms outlined in this Privacy Policy.</p>\n" +
                "\n" +
                "    <h2>Information We Collect</h2>\n" +
                "\n" +
                "    <p><strong>Personal Information:</strong></p>\n" +
                "    <ul>\n" +
                "        <li>Camera Access: We request permission to access your device's camera to enable the scanning of medication information and capturing images for medication reminders.</li>\n" +
                "        <li>Storage Access: We request permission to read and write to your device's storage to save and retrieve images related to medication reminders.</li>\n" +
                "    </ul>\n" +
                "\n" +
                "    <p><strong>Non-personal Information:</strong></p>\n" +
                "    <p>We may collect non-personal information, such as device information, usage statistics, and diagnostic data to improve the functionality and performance of the App.</p>\n" +
                "\n" +
                "    <h2>How We Use Your Information</h2>\n" +
                "\n" +
                "    <p><strong>Personal Information:</strong></p>\n" +
                "    <ul>\n" +
                "        <li>Camera Access: Used for scanning medication information and capturing images for medication reminders.</li>\n" +
                "        <li>Storage Access: Used to save and retrieve images related to medication reminders.</li>\n" +
                "    </ul>\n" +
                "\n" +
                "    <p><strong>Non-personal Information:</strong></p>\n" +
                "    <p>Used for analytical purposes to enhance user experience and improve the App's performance.</p>\n" +
                "\n" +
                "    <h2>Information Sharing</h2>\n" +
                "\n" +
                "    <p>We do not sell, trade, or otherwise transfer your personal information to outside parties. Your information may be shared with third-party service providers for the sole purpose of supporting the functionality of the App.</p>\n" +
                "\n" +
                "    <h2>Security</h2>\n" +
                "\n" +
                "    <p>We implement reasonable security measures to protect your personal information from unauthorized access, disclosure, alteration, and destruction.</p>\n" +
                "\n" +
                "    <h2>Children</h2>\n" +
                "\n" +
                "    <p>The App is not directed to children under 13. We do not knowingly collect personal information from anyone under 13 years of age.</p>\n" +
                "    <p>If we determine upon collection that a user is under this age, we will not use or maintain his/her personal information without the parent/guardian’s consent.</p>\n" +
                "\n" +
                "    <h2>Changes to This Privacy Policy</h2>\n" +
                "\n" +
                "    <p>We reserve the right to update this Privacy Policy at any time. You are encouraged to review this page periodically for any changes.</p>\n" +
                "\n" +
                "    <h2>Contact Us</h2>\n" +
                "\n" +
                "    <p>If you have any questions or concerns regarding this Privacy Policy, please contact us at <a href=\"mailto:rjfahad44@gmail.com\">rjfahad44@gmail.com</a>.</p>\n" +
                "\n" +
                "    <p>By using the TakeYourPill App, you acknowledge that you have read and understood this Privacy Policy.</p>\n" +
                "\n" +
                "</body>\n" +
                "\n" +
                "</html>"
    }
}