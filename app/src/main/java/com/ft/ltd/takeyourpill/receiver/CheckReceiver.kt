package com.ft.ltd.takeyourpill.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ft.ltd.takeyourpill.utils.Prefs
import dagger.hilt.android.AndroidEntryPoint
import com.ft.ltd.takeyourpill.utils.Constants
import com.ft.ltd.takeyourpill.reminder.ReminderManager
import com.ft.ltd.takeyourpill.reminder.ReminderUtil
import com.ft.ltd.takeyourpill.repository.HistoryRepository
import com.ft.ltd.takeyourpill.repository.PillRepository
import com.ft.ltd.takeyourpill.repository.ReminderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CheckReceiver : BroadcastReceiver() {

    @Inject
    lateinit var pillRepository: PillRepository

    @Inject
    lateinit var prefs: Prefs

    @Inject
    lateinit var reminderRepository: ReminderRepository

    @Inject
    lateinit var historyRepository: HistoryRepository

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(Constants.INTENT_EXTRA_REMINDER_ID, -1L)
        val remindedTime = intent.getLongExtra(Constants.INTENT_EXTRA_REMINDED_TIME, -1L)
        val checkCount = intent.getLongExtra(Constants.INTENT_CHECK_COUNT, -1L)

        if (remindedTime == -1L || reminderId == -1L || checkCount == -1L) {
            Timber.e("Invalid number of extras passed, exiting...")
            return
        }

        Timber.d(
            "Received reminder id: %d, remindedTime: %d, checkCount: %d",
            reminderId,
            remindedTime,
            checkCount
        )

        CoroutineScope(Dispatchers.IO).launch {
            val reminder = reminderRepository.getReminder(reminderId)
            val pill = pillRepository.getPill(reminder.pillId)

            ReminderUtil.createReminderNotification(context, pill, reminder, prefs)

            // If this reminder has not been confirmed and remindAgain is enabled, schedule check alarm
            historyRepository.getByPillIdAndTime(pill.id, remindedTime)?.let { history ->
                if (prefs.remindAgain && !history.hasBeenConfirmed && checkCount < Constants.MAX_CHECK_COUNT - 1) {
                    ReminderManager.createCheckAlarm(
                        prefs,
                        context,
                        reminderId,
                        remindedTime,
                        checkCount + 1
                    )
                }
            }
        }
    }
}