package com.rkopylknu.minimaltodo.domain.usecase.impl

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.rkopylknu.minimaltodo.util.FakeData
import com.rkopylknu.minimaltodo.util.ReminderService
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.Mockito.mock

class CreateAlarmUseCaseImplTest {

    @Test
    fun creates_reminder() {
        val context = mock(Context::class.java)
        val useCase = CreateAlarmUseCaseImpl(context)

        useCase.execute(FakeData.toDoItemWithReminder)

        assertNotNull(
            PendingIntent.getBroadcast(
                context, 0,
                Intent(ReminderService.ACTION_CREATE),
                PendingIntent.FLAG_NO_CREATE
            )
        )
    }

    @Test
    fun not_creates_reminder() {
        val context = mock(Context::class.java)
        val useCase = CreateAlarmUseCaseImpl(context)

        useCase.execute(FakeData.toDoItem)

        assertNull(
            PendingIntent.getBroadcast(
                context, 0,
                Intent(ReminderService.ACTION_CREATE),
                PendingIntent.FLAG_NO_CREATE
            )
        )
    }
}