package com.rkopylknu.minimaltodo.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.domain.usecase.CreateItemUseCase
import com.rkopylknu.minimaltodo.domain.usecase.UpdateItemUseCase
import kotlinx.datetime.*

class AddToDoViewModel(
    private val createItemUseCase: CreateItemUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    val toDoItem: ToDoItem?,
) : ViewModel() {

    var reminder: LocalDateTime? = toDoItem?.reminder
        private set

    fun setDefaultReminder() {
        val atNextHour = Clock.System.now()
            .plus(DateTimePeriod(hours = 1), TimeZone.UTC)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        reminder = LocalDateTime(
            atNextHour.date,
            LocalTime(atNextHour.hour + 1, minute = 0)
        )
    }

    fun resetReminder() {
        reminder = null
    }

    fun onDateSet(year: Int, month: Int, day: Int) {
        reminder = reminder?.run {
            LocalDateTime(
                date = LocalDate(year, month, day),
                time = time
            )
        }
    }

    fun onTimeSet(hour: Int, minute: Int) {
        reminder = reminder?.run {
            LocalDateTime(
                date = date,
                time = LocalTime(hour, minute)
            )
        }
    }

    fun onSaveItem(text: String, description: String) {
        val newItem = ToDoItem(
            id = CreateItemUseCase.EMPTY_ID,
            text = text,
            description = description,
            reminder = reminder,
            color = CreateItemUseCase.EMPTY_COLOR
        )
        toDoItem.let { oldItem ->
            if (oldItem == null) {
                createItemUseCase.execute(newItem)
            } else {
                updateItemUseCase.execute(oldItem, newItem)
            }
        }
    }

    fun getClipboardText(text: String, description: String) =
        "Title : $text\n" +
                "Description : $description\n" +
                " -Copied From MinimalToDo"

    class Factory(
        private val createItemUseCase: CreateItemUseCase,
        private val updateItemUseCase: UpdateItemUseCase,
        private val toDoItem: ToDoItem?,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddToDoViewModel::class.java)) {
                return AddToDoViewModel(
                    createItemUseCase,
                    updateItemUseCase,
                    toDoItem
                ) as T
            } else {
                throw IllegalArgumentException("Unexpected ViewModel class")
            }
        }
    }
}