package com.ft.ltd.takeyourpill.reminder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ft.ltd.takeyourpill.R
import com.ft.ltd.takeyourpill.utils.Prefs
import timber.log.Timber

object NotificationManager {

    fun createNotification(
        context: Context,
        title: String,
        description: String,
        color: Int,
        bitmap: Bitmap?,
        pendingIntent: PendingIntent,
        confirmPendingIntent: PendingIntent,
        delayPendingIntent: PendingIntent,
        fullscreenPendingIntent: PendingIntent,
        channelId: String,
        whenMillis: Long,
        prefs: Prefs
    ): Notification {


        val buttonDelay = prefs.buttonDelay
        // create notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_pill)
            .setContentTitle(title)
            .setContentText(description)
            .setColor(color)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setShowWhen(true)
            .setWhen(whenMillis)
            .addAction(
                NotificationCompat.Action(
                    R.drawable.ic_check,
                    context.getString(R.string.confirm),
                    confirmPendingIntent
                )
            )
            .addAction(
                NotificationCompat.Action(
                    R.drawable.ic_delay,
                    context.resources.getQuantityString(
                        R.plurals.delay,
                        buttonDelay,
                        buttonDelay
                    ),
                    delayPendingIntent
                )
            )

        if (prefs.alertStyle) {
            Timber.d("Using fullscreen intent")
            builder.setFullScreenIntent(fullscreenPendingIntent, true)
            builder.setCategory(NotificationCompat.CATEGORY_CALL)
        } else {
            builder.setContentIntent(pendingIntent)
        }

        // set notification style to BigPicture if the pill has a photo
        bitmap?.let {
            builder.setLargeIcon(bitmap)
            builder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(it)
                    //.bigLargeIcon(null)
            )
        }
        return builder.build()
    }

    fun showNotification(
        context: Context,
        title: String,
        description: String,
        color: Int,
        bitmap: Bitmap?,
        pendingIntent: PendingIntent,
        confirmPendingIntent: PendingIntent,
        delayPendingIntent: PendingIntent,
        fullscreenPendingIntent: PendingIntent,
        notificationId: Long,
        channelId: String,
        whenMillis: Long
    ) {
        Timber.d("Creating notification for reminderId %d", notificationId)

        val prefs = Prefs(context)

        val notification = createNotification(
            context,
            title,
            description,
            color,
            bitmap,
            pendingIntent,
            confirmPendingIntent,
            delayPendingIntent,
            fullscreenPendingIntent,
            channelId,
            whenMillis,
            prefs,
        )

        // show notification to the user
//        with(NotificationManagerCompat.from(context)) {
//            notify(notificationId.toInt(), notification)
//        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId.toInt(), notification)

    }

    fun cancelNotification(context: Context, notificationId: Long) =
        NotificationManagerCompat.from(context).cancel(notificationId.toInt())

    fun createNotificationChannel(context: Context, id: String, name: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance)
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun removeNotificationChannel(context: Context, id: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.deleteNotificationChannel(id)
        }
    }

}