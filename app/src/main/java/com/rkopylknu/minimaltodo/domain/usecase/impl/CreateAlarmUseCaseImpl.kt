package com.rkopylknu.minimaltodo.domain.usecase.impl

import android.content.Context
import android.content.Intent
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.domain.usecase.CreateAlarmUseCase
import com.rkopylknu.minimaltodo.util.ReminderService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CreateAlarmUseCaseImpl(
    private val appContext: Context
) : CreateAlarmUseCase {

    override fun execute(item: ToDoItem) {
        if (item.reminder == null) return

        val createAlarmIntent = Intent(
            appContext,
            ReminderService::class.java
        ).apply {
            action = ReminderService.ACTION_CREATE
            putExtra(
                ReminderService.TO_DO_ITEM_KEY,
                Json.encodeToString(item)
            )
        }
        appContext.startService(createAlarmIntent)
    }
}