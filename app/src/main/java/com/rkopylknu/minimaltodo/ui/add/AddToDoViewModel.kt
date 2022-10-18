package com.rkopylknu.minimaltodo.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.domain.usecase.CreateItemUseCase
import com.rkopylknu.minimaltodo.domain.usecase.UpdateItemUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.*

class AddToDoViewModel @AssistedInject constructor(
    private val createItemUseCase: CreateItemUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    @Assisted val toDoItem: ToDoItem?,
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

    fun onSaveItem(text: String, description: String, isPrior: Boolean) {
        val currentItem = toDoItem

        if (currentItem == null) {
            val newItem = ToDoItem(
                text = text,
                description = description,
                isPrior = isPrior,
                reminder = reminder
            )
            viewModelScope.launch(Dispatchers.IO) {
                createItemUseCase.execute(newItem)
            }
        } else {
            val updatedItem = currentItem.copy(
                text = text,
                description = description,
                isPrior = isPrior,
                reminder = reminder
            )
            viewModelScope.launch(Dispatchers.IO) {
                updateItemUseCase.execute(updatedItem)
            }
        }
    }

    fun getClipboardText(text: String, description: String) =
        "Title : $text\n" +
                "Description : $description\n" +
                " -Copied From MinimalToDo"

    @AssistedFactory
    interface DaggerFactory {

        fun create(toDoItem: ToDoItem?): AddToDoViewModel
    }

    companion object {

        fun getFactory(daggerFactory: DaggerFactory, toDoItem: ToDoItem?) =
            object : ViewModelProvider.Factory {

                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return daggerFactory.create(toDoItem) as T
                }
            }
    }
}