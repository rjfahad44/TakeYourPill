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
        webView.loadUrl(urlString)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {

//                if (request.url.toString().contains("http://sites.google.com/view/")) {
//                    val intent = Intent(Intent.ACTION_VIEW)
//                    intent.data = request.url
//                    if (intent.resolveActivity(packageManager) != null) {
//                        startActivity(intent)
//                        return true
//                    }
//                }

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
    }
}