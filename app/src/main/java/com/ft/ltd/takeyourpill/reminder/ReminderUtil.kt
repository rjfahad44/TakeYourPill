package com.ft.ltd.takeyourpill.reminder

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavDeepLinkBuilder
import com.ft.ltd.takeyourpill.R
import com.ft.ltd.takeyourpill.activity.MainActivity
import com.ft.ltd.takeyourpill.utils.Prefs
import com.ft.ltd.takeyourpill.utils.Constants
import com.ft.ltd.takeyourpill.model.Pill
import com.ft.ltd.takeyourpill.model.Reminder
import com.ft.ltd.takeyourpill.receiver.CheckReceiver
import com.ft.ltd.takeyourpill.receiver.ConfirmReceiver
import com.ft.ltd.takeyourpill.receiver.DelayReceiver
import com.ft.ltd.takeyourpill.receiver.ReminderReceiver
import timber.log.Timber

object ReminderUtil {
    private fun getNotificationClickIntent(context: Context, pillId: Long): PendingIntent {
        val args = Bundle()
        args.putLong(Constants.INTENT_EXTRA_PILL_ID, pillId)
        args.putBoolean(Constants.INTENT_EXTRA_LAUNCHED_FROM_NOTIFICATION, true)
        return NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.navigation_graph)
            .setDestination(R.id.details)
            .setArguments(args)
            .createPendingIntent()
    }

    fun getConfirmIntent(
        context: Context,
        reminderId: Long,
        pillId: Long,
        remindedTime: Long
    ) = Intent(context, ConfirmReceiver::class.java).apply {
        putExtra(Constants.INTENT_EXTRA_REMINDER_ID, reminderId)
        putExtra(Constants.INTENT_EXTRA_PILL_ID, pillId)
        putExtra(Constants.INTENT_EXTRA_REMINDED_TIME, remindedTime)
    }


    private fun getNotificationConfirmIntent(
        context: Context,
        reminderId: Long,
        pillId: Long,
        remindedTime: Long
    ) = PendingIntent.getBroadcast(
        context,
        reminderId.toInt(),
        getConfirmIntent(context, reminderId, pillId, remindedTime),
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun getNotificationDelayIntent(
        context: Context,
        reminderId: Long,
        delayByMillis: Long,
        remindedTime: Long
    ): PendingIntent =
        Intent(context, DelayReceiver::class.java).let { intent ->
            intent.putExtra(Constants.INTENT_EXTRA_REMINDER_ID, reminderId)
            intent.putExtra(Constants.INTENT_EXTRA_TIME_DELAY, delayByMillis)
            intent.putExtra(Constants.INTENT_EXTRA_REMINDED_TIME, remindedTime)
            PendingIntent.getBroadcast(
                context,
                reminderId.toInt(),
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

    fun getAlarmAgainIntent(
        context: Context,
        reminderId: Long,
        remindedTime: Long,
        checkCount: Long,
    ): PendingIntent = Intent(context, CheckReceiver::class.java).let { intent ->
        intent.putExtra(Constants.INTENT_EXTRA_REMINDER_ID, reminderId)
        intent.putExtra(Constants.INTENT_EXTRA_REMINDED_TIME, remindedTime)
        intent.putExtra(Constants.INTENT_CHECK_COUNT, checkCount)
        PendingIntent.getBroadcast(context, reminderId.toInt(), intent,  PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
    }

    fun getAlarmIntent(
        context: Context,
        reminderId: Long
    ): PendingIntent =
        Intent(context, ReminderReceiver::class.java).let { intent ->
            intent.putExtra(Constants.INTENT_EXTRA_REMINDER_ID, reminderId)
            PendingIntent.getBroadcast(
                context,
                reminderId.toInt(),
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

    fun createReminderNotification(context: Context, pill: Pill, reminder: Reminder, prefs: Prefs) {
        Timber.d("Creating reminder notification")
        NotificationManager.showNotification(
            context,
            title = pill.name,
            description = pill.getNotificationDescription(context, reminder),
            color = pill.color.getColor(context),
            bitmap = pill.photo,
            pendingIntent = getNotificationClickIntent(context, pill.id),
            confirmPendingIntent = getNotificationConfirmIntent(
                context,
                reminder.id,
                pill.id,
                reminder.getTodayMillis()
            ),
            delayPendingIntent = getNotificationDelayIntent(
                context,
                reminder.id,
                prefs.buttonDelay.toLong() * 1000 * 60,
                reminder.getTodayCalendar().timeInMillis
            ),
            fullscreenPendingIntent = getNotificationClickIntent(context, pill.id),
            notificationId = reminder.id,
            channelId = pill.id.toString(),
            whenMillis = reminder.getTodayMillis()
        )
    }

    fun getFullscreenNotification(context: Context, pill: Pill, reminder: Reminder, prefs: Prefs): Notification {
        Timber.d("Creating fullscreen notification")
        return NotificationManager.createNotification(
            context,
            title = pill.name,
            description = pill.getNotificationDescription(context, reminder),
            color = pill.color.getColor(context),
            bitmap = pill.photo,
            pendingIntent = getNotificationClickIntent(context, pill.id),
            confirmPendingIntent = getNotificationConfirmIntent(
                context,
                reminder.id,
                pill.id,
                reminder.getTodayMillis()
            ),
            delayPendingIntent = getNotificationDelayIntent(
                context,
                reminder.id,
                prefs.buttonDelay.toLong() * 1000 * 60,
                reminder.getTodayCalendar().timeInMillis
            ),
            fullscreenPendingIntent = getNotificationClickIntent(context, pill.id),
            channelId = pill.id.toString(),
            whenMillis = reminder.getTodayMillis(),
            prefs = prefs
        )
    }
}