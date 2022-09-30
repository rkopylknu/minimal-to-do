package com.rkopylknu.minimaltodo.ui.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.domain.usecase.DeleteItemUseCase
import com.rkopylknu.minimaltodo.domain.usecase.UpdateItemUseCase
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

class ReminderViewModel(
    private val deleteItemUseCase: DeleteItemUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    val toDoItem: ToDoItem
) : ViewModel() {

    var snoozeOption: Int = SNOOZE_OPTIONS.first()
        private set

    fun onSetSnoozeOption(index: Int) {
        snoozeOption = SNOOZE_OPTIONS[index]
    }

    fun onDeleteItem() {
        deleteItemUseCase.execute(toDoItem)
    }

    fun onDelayReminder() {
        val delayedReminder = TimeZone.currentSystemDefault().run {
            toDoItem.reminder!!.toInstant()
                .plus(DateTimePeriod(minutes = snoozeOption), this)
                .toLocalDateTime()
        }
        val newToDoItem = toDoItem.copy(reminder = delayedReminder)

        updateItemUseCase.execute(toDoItem, newToDoItem)
    }

    class Factory(
        private val deleteItemUseCase: DeleteItemUseCase,
        private val updateItemUseCase: UpdateItemUseCase,
        val toDoItem: ToDoItem
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
                return ReminderViewModel(
                    deleteItemUseCase,
                    updateItemUseCase,
                    toDoItem
                ) as T
            } else {
                throw IllegalArgumentException("Unexpected ViewModel class")
            }
        }
    }

    companion object {

        val SNOOZE_OPTIONS = listOf(10, 30, 60)
    }
}