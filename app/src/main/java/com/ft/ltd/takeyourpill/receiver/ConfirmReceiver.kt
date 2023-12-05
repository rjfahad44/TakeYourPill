package com.ft.ltd.takeyourpill.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import com.ft.ltd.takeyourpill.R
import com.ft.ltd.takeyourpill.utils.Prefs
import com.ft.ltd.takeyourpill.utils.Constants
import com.ft.ltd.takeyourpill.reminder.NotificationManager
import com.ft.ltd.takeyourpill.reminder.ReminderManager
import com.ft.ltd.takeyourpill.reminder.ReminderUtil
import com.ft.ltd.takeyourpill.repository.HistoryRepository
import com.ft.ltd.takeyourpill.service.FullscreenService
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ConfirmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var historyRepository: HistoryRepository

    @Inject
    lateinit var prefs: Prefs

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(Constants.INTENT_EXTRA_REMINDER_ID, -1L)
        val pillId = intent.getLongExtra(Constants.INTENT_EXTRA_PILL_ID, -1L)
        val remindedTime = intent.getLongExtra(Constants.INTENT_EXTRA_REMINDED_TIME, -1L)

        if (reminderId == -1L || pillId == -1L || remindedTime == -1L) {
            Timber.e("Invalid number of extras passed, exiting...")
            return
        }

        var success = false
        CoroutineScope(Dispatchers.IO).launch{

            historyRepository.getByPillIdAndTime(pillId, remindedTime)?.let { history ->
                history.confirmed = Calendar.getInstance()
                historyRepository.updateHistoryItem(history)
                success = true
            } ?: run {
                Timber.e("Couldn't find the correct history item...")
            }

            if (prefs.alertStyle) {
                FullscreenService.stopService(context)
            }

            withContext(Dispatchers.Main) {
                if (success) {
                    // Cancel check alarm
                    val again =
                        ReminderUtil.getAlarmAgainIntent(context, reminderId, remindedTime, 0)
                    again.cancel()
                    ReminderManager.getAlarmManager(context).cancel(again)
                    // Hide notification
                    NotificationManager.cancelNotification(context, reminderId)
                    Toast.makeText(
                        context,
                        context.getString(R.string.confirmed),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}