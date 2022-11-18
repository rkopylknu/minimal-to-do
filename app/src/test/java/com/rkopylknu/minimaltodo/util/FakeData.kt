package com.rkopylknu.minimaltodo.util

import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

object FakeData {

    val toDoItem = ToDoItem(
        text = "Test",
        description = "Test",
        isPrior = false,
        reminder = null
    )

    val toDoItemInvalid = ToDoItem(
        text = "",
        description = "Test",
        isPrior = false,
        reminder = null
    )

    val toDoItemWithReminder = ToDoItem(
        text = "Test",
        description = "Test",
        isPrior = false,
        reminder = LocalDateTime(
            LocalDate(year = 9999, monthNumber = 1, dayOfMonth = 1),
            LocalTime(hour = 0, minute = 0, second = 0)
        )
    )

    val toDoItemList = listOf(
        ToDoItem(
            id = 1L,
            text = "Item 1",
            description = "Description 1",
            reminder = null,
            isPrior = false
        ),
        ToDoItem(
            id = 2L,
            text = "Item 2",
            description = "Description 2",
            reminder = LocalDateTime(
                LocalDate(year = 9999, monthNumber = 1, dayOfMonth = 1),
                LocalTime(hour = 0, minute = 0, second = 0)
            ),
            isPrior = false
        ),
        ToDoItem(
            id = 3L,
            text = "Item 3",
            description = "Description 3",
            reminder = null,
            isPrior = true
        ),
    )
}