package com.rkopylknu.minimaltodo.data.repository

import com.rkopylknu.minimaltodo.domain.model.ToDoItem

interface ToDoItemRepository {

    fun load(): List<ToDoItem>

    fun save(toDoItems: List<ToDoItem>)

    fun getValidId(): Long

    fun mutate(transformation: MutableList<ToDoItem>.() -> Unit)
}