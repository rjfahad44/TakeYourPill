package com.ft.ltd.takeyourpill.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ft.ltd.takeyourpill.utils.Prefs
import dagger.hilt.android.AndroidEntryPoint
import com.ft.ltd.takeyourpill.utils.Constants
import com.ft.ltd.takeyourpill.utils.getTimeString
import com.ft.ltd.takeyourpill.reminder.NotificationManager
import com.ft.ltd.takeyourpill.reminder.ReminderManager
import com.ft.ltd.takeyourpill.reminder.ReminderUtil
import com.ft.ltd.takeyourpill.service.FullscreenService
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class DelayReceiver : BroadcastReceiver() {

    @Inject
    lateinit var prefs: Prefs

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(Constants.INTENT_EXTRA_REMINDER_ID, -1L)
        val remindedTime = intent.getLongExtra(Constants.INTENT_EXTRA_REMINDED_TIME, -1L)
        val delayByMillis = intent.getLongExtra(Constants.INTENT_EXTRA_TIME_DELAY, -1L)

        if (delayByMillis == -1L || remindedTime == -1L || reminderId == -1L) {
            Timber.e("Invalid number of extras passed, exiting...")
            return
        }

        if (prefs.alertStyle) {
            FullscreenService.stopService(context)
        } else {
            // Cancel check reminder
            val again = ReminderUtil.getAlarmAgainIntent(context, reminderId, remindedTime, 0)
            again.cancel()
            ReminderManager.getAlarmManager(context).cancel(again)
        }

        ReminderManager.createCheckAlarm(
            prefs,
            context,
            reminderId,
            remindedTime,
            0, // Reset check counter
            delayByMillis
        )
        Timber.d(
            "Check alarm has been set to start in %s minutes",
            delayByMillis.getTimeString()
        )
        NotificationManager.cancelNotification(context, reminderId)
    }
}