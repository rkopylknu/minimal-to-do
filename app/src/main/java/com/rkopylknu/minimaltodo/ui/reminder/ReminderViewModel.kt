package com.rkopylknu.minimaltodo.ui.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.domain.usecase.DeleteItemUseCase
import com.rkopylknu.minimaltodo.domain.usecase.UpdateItemUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

class ReminderViewModel @AssistedInject constructor(
    private val deleteItemUseCase: DeleteItemUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    @Assisted val toDoItem: ToDoItem
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

    @AssistedFactory
    interface DaggerFactory {

        fun create(toDoItem: ToDoItem): ReminderViewModel
    }

    companion object {

        val SNOOZE_OPTIONS = listOf(10, 30, 60)

        fun getFactory(daggerFactory: DaggerFactory, toDoItem: ToDoItem) =
            object : ViewModelProvider.Factory {

                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return daggerFactory.create(toDoItem) as T
                }
            }
    }
}