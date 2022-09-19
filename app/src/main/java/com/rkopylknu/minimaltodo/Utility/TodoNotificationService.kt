package com.rkopylknu.minimaltodo.Utility

import android.app.IntentService
import android.content.Intent
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.Reminder.ReminderActivity
import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import android.app.NotificationManager
import androidx.core.content.ContextCompat
import android.os.Build
import android.app.NotificationChannel
import android.content.Context
import java.util.*

class TodoNotificationService : IntentService("TodoNotificationService") {

    private var mTodoText: String? = null
    private var mTodoUUID: UUID? = null

    override fun onHandleIntent(intent: Intent?) {
        mTodoText = intent!!.getStringExtra(TODOTEXT)
        mTodoUUID = intent.getSerializableExtra(TODOUUID) as UUID?
        val contentIntent = Intent(this, ReminderActivity::class.java)
        contentIntent.putExtra(TODOUUID, mTodoUUID)

        val pendingContentIntent = PendingIntent.getActivity(
            this,
            mTodoUUID.hashCode(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val deleteIntent = Intent(this, DeleteNotificationService::class.java)
        deleteIntent.putExtra(TODOUUID, mTodoUUID)
        val pendingDeleteIntent = PendingIntent.getService(
            this,
            mTodoUUID.hashCode(),
            deleteIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(mTodoText)
            .setContentText("")
            .setSmallIcon(R.drawable.ic_done_white_24dp)
            .setContentIntent(pendingContentIntent)
            .setDeleteIntent(pendingDeleteIntent)
            .setAutoCancel(true)
            .build()
        val manager = ContextCompat.getSystemService(this, NotificationManager::class.java)
        manager!!.notify(mTodoUUID.hashCode(), notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, "Channel Name", importance)
            val notificationManager =
                ContextCompat.getSystemService(this, NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }

    companion object {
        const val TODOTEXT = "com.avjindersekhon.todonotificationservicetext"
        const val TODOUUID = "com.avjindersekhon.todonotificationserviceuuid"
        private const val CHANNEL_ID = "com.rkopylknu.minimaltodo"
    }
}