package com.ft.ltd.takeyourpill.activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.ft.ltd.takeyourpill.BuildConfig
import com.ft.ltd.takeyourpill.R
import com.ft.ltd.takeyourpill.ad_manager.admob.AdMobAdsManager
import com.ft.ltd.takeyourpill.databinding.ActivityMainBinding
import com.ft.ltd.takeyourpill.utils.Prefs
import com.ft.ltd.takeyourpill.utils.Utils
import com.ft.ltd.takeyourpill.utils.toast
import com.ft.ltd.takeyourpill.utils.viewBinding
import com.ft.ltd.takeyourpill.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val model: MainViewModel by viewModels()
    private var appUpdateManager: AppUpdateManager? = null

    @Inject
    lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize()
    }

    private fun initialize() {
        if (prefs.firstRun) {
            val intent = Intent(this, AppIntroActivity::class.java)
            introResult.launch(intent)
        }

        model.adMobAdsManager = AdMobAdsManager(this)

        setTheme(R.style.AppTheme) // Switch from splash theme
        setContentView(binding.root)
        Utils.setTheme(prefs.theme)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            showBottomBar(
                when (destination.id) {
                    R.id.homescreen, R.id.history, R.id.settings -> true
                    else -> false
                }
            )
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            model.scrollUp()
            true
        }

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
        model.planReminders(applicationContext)
    }

    private fun showBottomBar(visible: Boolean) {
        if (binding.bottomNavigation.isVisible == visible) return
        binding.bottomNavigation.isVisible = visible
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private val introResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            when (result.resultCode) {
                RESULT_OK -> {
                    prefs.firstRun = false
                    notificationPermission()
                }

                else -> {
                    finish()
                }
            }
        }

    private fun notificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                Snackbar.make(
                    binding.root, "please on \"Notifications\"", 9000
                ).setAction("Settings") {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }.show()
            } else {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


    private val requestNotificationPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                //permission granted
                toast("Permission granted ((;")
            } else {
                //permission denied
                toast("Permission denied (:")
            }
        }


    private val DAYS_FOR_FLEXIBLE_UPDATE = 7 // 7 days
    private fun checkAppVersionUpdate() {
        if (BuildConfig.DEBUG) { // no need to show dialog for debug build
            return
        }
        appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo

        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            if (
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                (appUpdateInfo.clientVersionStalenessDays() ?: -1) >= DAYS_FOR_FLEXIBLE_UPDATE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {

                val updateType = if (appUpdateInfo.updatePriority() >= 4) {
                    AppUpdateType.IMMEDIATE
                } else {
                    AppUpdateType.FLEXIBLE
                }

                appUpdateManager?.startUpdateFlowForResult(
                    appUpdateInfo,
                    activityResultLauncher,
                    AppUpdateOptions.newBuilder(updateType)
                        .setAllowAssetPackDeletion(true)
                        .build()
                )
            }
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode != RESULT_OK) {
                appUpdateManager?.registerListener(installStateListener)
            }
        }

    private val installStateListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADING) {
            val bytesDownloaded = state.bytesDownloaded()
            val totalBytesToDownload = state.totalBytesToDownload()
        }
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            appUpdateManager?.completeUpdate()
        }
    }


    override fun onResume() {
        super.onResume()
        if (!prefs.firstRun) {
            notificationPermission()
            checkAppVersionUpdate()
//            model.adMobAdsManager.showInterstitialAd(this)
//            adMobAdsManager.showRewardedAd{
//                Log.d(TAG, "type : ${it.rewardItem.type}")
//                Log.d(TAG, "amount : ${it.rewardItem.amount}")
//                Log.d(TAG, "adUnitId : ${it.adUnitId}")
//            }
        }

        appUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                appUpdateManager?.completeUpdate()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        appUpdateManager?.unregisterListener(installStateListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        //model.adMobAdsManager.destroy()
        appUpdateManager?.unregisterListener(installStateListener)
    }
}