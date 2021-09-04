package com.udacity.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R

const val FILE_NAME = "fileName"
const val STATUS = "status"
private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(
    messageBody: String,
    applicationContext: Context,
    status: DownloadStatus
) {
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.apply {
        putExtra(FILE_NAME, messageBody)
        putExtra(STATUS, status)
    }

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    val action = NotificationCompat.Action.Builder(
        0,
        applicationContext.getString(R.string.check_status),
        contentPendingIntent
    ).build()
    val notificationBuilder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.downloading_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.download_complete))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .addAction(action)
    notify(NOTIFICATION_ID, notificationBuilder.build())
}
