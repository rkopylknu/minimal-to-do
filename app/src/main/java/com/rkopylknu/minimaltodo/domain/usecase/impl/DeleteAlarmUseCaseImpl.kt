package com.rkopylknu.minimaltodo.domain.usecase.impl

import android.content.Context
import android.content.Intent
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.domain.usecase.DeleteAlarmUseCase
import com.rkopylknu.minimaltodo.util.ReminderService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DeleteAlarmUseCaseImpl @Inject constructor(
    @ApplicationContext private val appContext: Context
): DeleteAlarmUseCase {

    override fun execute(item: ToDoItem) {
        if (item.reminder == null) return

        val cancelAlarmIntent = Intent(
            appContext,
            ReminderService::class.java
        ).apply {
            action = ReminderService.ACTION_CANCEL
            putExtra(
                ReminderService.TO_DO_ITEM_KEY,
                Json.encodeToString(item)
            )
        }
        appContext.startService(cancelAlarmIntent)
    }
}