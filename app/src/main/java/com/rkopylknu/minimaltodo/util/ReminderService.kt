package com.rkopylknu.minimaltodo.util

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.rkopylknu.minimaltodo.App
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.domain.usecase.UpdateItemUseCase
import com.rkopylknu.minimaltodo.domain.usecase.impl.*
import com.rkopylknu.minimaltodo.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@AndroidEntryPoint
class ReminderService : IntentService(REMINDER_SERVICE_NAME) {

    @Inject
    lateinit var updateItemUseCase: UpdateItemUseCase

    private var alarms = emptyMap<Long, PendingIntent>()

    override fun onHandleIntent(intent: Intent?) {
        intent ?: return

        val toDoItem = Json.decodeFromString<ToDoItem>(
            intent.getStringExtra(TO_DO_ITEM_KEY)!!
        )

        when (intent.action) {
            ACTION_CREATE -> createAlarm(toDoItem)
            ACTION_CANCEL -> cancelAlarm(toDoItem)
            ACTION_NOTIFY -> displayAlarm(toDoItem)
            ACTION_DELETE -> deleteAlarm(toDoItem)
            else -> throw IllegalStateException("Unexpected action")
        }
    }

    private fun createAlarm(toDoItem: ToDoItem) {
        val reminderTimeMillis = toDoItem.reminder
            ?.toInstant(TimeZone.currentSystemDefault())
            ?.run { toEpochMilliseconds() }
            ?: return

        val displayAlarmIntent: PendingIntent = PendingIntent
            .getService(
                this,
                toDoItem.hashCode(),
                Intent(ACTION_NOTIFY)
                    .apply {
                        putExtra(
                            TO_DO_ITEM_KEY,
                            Json.encodeToString(toDoItem)
                        )
                    },
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            reminderTimeMillis,
            displayAlarmIntent
        )
        alarms = alarms.plus(toDoItem.id to displayAlarmIntent)
    }

    private fun cancelAlarm(toDoItem: ToDoItem) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarms[toDoItem.id]?.let { displayAlarmIntent ->
            alarmManager.cancel(displayAlarmIntent)
        }
        alarms = alarms.minus(toDoItem.id)
    }

    private fun displayAlarm(toDoItem: ToDoItem) {
        val showReminderIntent = PendingIntent.getActivity(
            this,
            toDoItem.hashCode(),
            Intent(this, MainActivity::class.java).apply {
                action = MainActivity.SHOW_REMINDER_ACTION
                putExtra(
                    MainActivity.TO_DO_ITEM_KEY,
                    Json.encodeToString(toDoItem)
                )
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val deleteReminderIntent = PendingIntent.getService(
            this,
            toDoItem.hashCode(),
            Intent(this, ReminderService::class.java)
                .apply {
                    action = ACTION_DELETE
                    putExtra(TO_DO_ITEM_KEY, Json.encodeToString(toDoItem))
                },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        setupNotificationChannel()
        val notification = NotificationCompat
            .Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(toDoItem.text)
            .setContentText("")
            .setSmallIcon(R.drawable.ic_done_filled)
            .setContentIntent(showReminderIntent)
            .setDeleteIntent(deleteReminderIntent)
            .setAutoCancel(true)
            .build()

        ContextCompat
            .getSystemService(this, NotificationManager::class.java)!!
            .notify(toDoItem.hashCode(), notification)
    }

    private fun deleteAlarm(toDoItem: ToDoItem) {
        cancelAlarm(toDoItem)

        updateItemUseCase.execute(
            toDoItem,
            toDoItem.copy(reminder = null)
        )
    }

    private fun setupNotificationChannel() {
        // notification channel isn't needed
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            importance
        )

        ContextCompat
            .getSystemService(this, NotificationManager::class.java)!!
            .createNotificationChannel(channel)
    }

    companion object {

        const val ACTION_CREATE =
            "com.rkopylknu.minimaltodo.data.local.ReminderNotificationService.CREATE"
        const val ACTION_CANCEL =
            "com.rkopylknu.minimaltodo.data.local.ReminderNotificationService.CANCEL"
        const val ACTION_NOTIFY =
            "com.rkopylknu.minimaltodo.data.local.ReminderNotificationService.NOTIFY"
        const val ACTION_DELETE =
            "com.rkopylknu.minimaltodo.data.local.ReminderNotificationService.DELETE"

        const val TO_DO_ITEM_KEY = "to_do_item"

        private const val NOTIFICATION_CHANNEL_NAME = "Reminder"
        private const val NOTIFICATION_CHANNEL_ID =
            "com.rkopylknu.minimaltodo.data.local.ReminderNotificationService"
    }
}